package com.realestate.property.service;

import com.realestate.property.entity.BlogPost;
import com.realestate.property.entity.Property;
import com.realestate.property.repository.BlogPostRepository;
import com.realestate.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour générer les sitemaps XML dynamiquement
 * Optimisé pour le SEO 2024-2025
 */
@Service
public class SitemapService {

    private static final Logger logger = LoggerFactory.getLogger(SitemapService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int MAX_PROPERTIES_PER_SITEMAP = 50000; // Limite Google

    private final PropertyRepository propertyRepository;
    private final BlogPostRepository blogPostRepository;

    public SitemapService(PropertyRepository propertyRepository, BlogPostRepository blogPostRepository) {
        this.propertyRepository = propertyRepository;
        this.blogPostRepository = blogPostRepository;
    }

    /**
     * Génère le sitemap principal avec les pages statiques
     */
    @Transactional(readOnly = true)
    public String generateMainSitemap(String baseUrl) {
        List<SitemapUrl> urls = new ArrayList<>();
        
        // Page d'accueil
        urls.add(new SitemapUrl(
            baseUrl,
            LocalDateTime.now().format(DATE_FORMATTER),
            "daily",
            1.0
        ));
        
        // Page de recherche
        urls.add(new SitemapUrl(
            baseUrl + "/search",
            LocalDateTime.now().format(DATE_FORMATTER),
            "hourly",
            0.9
        ));
        
        // Page blog
        urls.add(new SitemapUrl(
            baseUrl + "/blog",
            LocalDateTime.now().format(DATE_FORMATTER),
            "daily",
            0.8
        ));
        
        // Pages statiques
        urls.add(new SitemapUrl(
            baseUrl + "/about",
            LocalDateTime.now().format(DATE_FORMATTER),
            "monthly",
            0.7
        ));
        
        urls.add(new SitemapUrl(
            baseUrl + "/publish",
            LocalDateTime.now().format(DATE_FORMATTER),
            "monthly",
            0.6
        ));
        
        return generateSitemapXML(urls);
    }

    /**
     * Génère le sitemap des propriétés (peut être paginé si > 50k propriétés)
     */
    @Transactional(readOnly = true)
    public String generatePropertiesSitemap(String baseUrl, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        // Récupérer uniquement les propriétés publiées
        Page<Property> properties = propertyRepository.findByStatusIn(
            List.of("PUBLISHED", "AVAILABLE"),
            pageable
        );
        
        List<SitemapUrl> urls = new ArrayList<>();
        
        for (Property property : properties.getContent()) {
            String propertyUrl = baseUrl + "/property/" + property.getId();
            String lastmod = property.getUpdatedAt() != null
                ? property.getUpdatedAt().format(DATE_FORMATTER)
                : property.getCreatedAt().format(DATE_FORMATTER);
            
            // Priorité basée sur le statut et la date de mise à jour
            double priority = property.getStatus().equals("PUBLISHED") ? 0.8 : 0.5;
            
            urls.add(new SitemapUrl(
                propertyUrl,
                lastmod,
                "weekly",
                priority
            ));
        }
        
        return generateSitemapXML(urls);
    }

    /**
     * Génère un sitemap index si nécessaire (pour > 50k propriétés)
     */
    @Transactional(readOnly = true)
    public String generateSitemapIndex(String baseUrl) {
        long totalProperties = propertyRepository.countByStatusIn(List.of("PUBLISHED", "AVAILABLE"));
        int sitemapsNeeded = (int) Math.ceil((double) totalProperties / MAX_PROPERTIES_PER_SITEMAP);
        
        if (sitemapsNeeded <= 1) {
            // Pas besoin d'index, retourner le sitemap principal
            return generateMainSitemap(baseUrl);
        }
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        
        // Sitemap principal
        xml.append("  <sitemap>\n");
        xml.append("    <loc>").append(baseUrl).append("/sitemap.xml</loc>\n");
        xml.append("    <lastmod>").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</lastmod>\n");
        xml.append("  </sitemap>\n");
        
        // Sitemaps des propriétés (paginés)
        for (int i = 0; i < sitemapsNeeded; i++) {
            xml.append("  <sitemap>\n");
            xml.append("    <loc>").append(baseUrl).append("/sitemap-properties-").append(i).append(".xml</loc>\n");
            xml.append("    <lastmod>").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</lastmod>\n");
            xml.append("  </sitemap>\n");
        }
        
        // Sitemap des articles de blog
        xml.append("  <sitemap>\n");
        xml.append("    <loc>").append(baseUrl).append("/sitemap-blog.xml</loc>\n");
        xml.append("    <lastmod>").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</lastmod>\n");
        xml.append("  </sitemap>\n");
        
        xml.append("</sitemapindex>");
        return xml.toString();
    }

    /**
     * Génère le sitemap des articles de blog
     */
    @Transactional(readOnly = true)
    public String generateBlogSitemap(String baseUrl) {
        List<BlogPost> posts = blogPostRepository.findAllPublishedPosts(LocalDateTime.now());
        
        List<SitemapUrl> urls = new ArrayList<>();
        
        for (BlogPost post : posts) {
            // Utiliser le slug pour l'URL SEO-friendly
            String postUrl = baseUrl + "/blog/" + post.getSlug();
            String lastmod = post.getUpdatedAt() != null
                ? post.getUpdatedAt().format(DATE_FORMATTER)
                : (post.getPublishedAt() != null 
                    ? post.getPublishedAt().format(DATE_FORMATTER)
                    : post.getCreatedAt().format(DATE_FORMATTER));
            
            // Priorité basée sur la date de publication (articles récents = priorité plus élevée)
            double priority = 0.7; // Priorité de base pour les articles de blog
            
            urls.add(new SitemapUrl(
                postUrl,
                lastmod,
                "weekly",
                priority
            ));
        }
        
        return generateSitemapXML(urls);
    }

    /**
     * Génère le XML du sitemap à partir d'une liste d'URLs
     */
    private String generateSitemapXML(List<SitemapUrl> urls) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n");
        xml.append("        xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\"\n");
        xml.append("        xmlns:news=\"http://www.google.com/schemas/sitemap-news/0.9\">\n");
        
        for (SitemapUrl url : urls) {
            xml.append("  <url>\n");
            xml.append("    <loc>").append(escapeXML(url.loc)).append("</loc>\n");
            if (url.lastmod != null) {
                xml.append("    <lastmod>").append(url.lastmod).append("</lastmod>\n");
            }
            if (url.changefreq != null) {
                xml.append("    <changefreq>").append(url.changefreq).append("</changefreq>\n");
            }
            if (url.priority != null) {
                xml.append("    <priority>").append(url.priority).append("</priority>\n");
            }
            xml.append("  </url>\n");
        }
        
        xml.append("</urlset>");
        return xml.toString();
    }

    /**
     * Échappe les caractères XML spéciaux
     */
    private String escapeXML(String str) {
        return str
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }

    /**
     * Classe interne pour représenter une URL de sitemap
     */
    private static class SitemapUrl {
        String loc;
        String lastmod;
        String changefreq;
        Double priority;

        SitemapUrl(String loc, String lastmod, String changefreq, Double priority) {
            this.loc = loc;
            this.lastmod = lastmod;
            this.changefreq = changefreq;
            this.priority = priority;
        }
    }
}

