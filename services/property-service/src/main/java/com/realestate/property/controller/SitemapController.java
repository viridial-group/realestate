package com.realestate.property.controller;

import com.realestate.property.service.SitemapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour générer les sitemaps XML dynamiquement
 * Optimisé pour le SEO 2024-2025
 */
@RestController
@RequestMapping("/api/public")
@Tag(name = "Sitemap", description = "Dynamic sitemap generation for SEO")
public class SitemapController {

    private final SitemapService sitemapService;
    
    @Value("${app.base-url:http://viridial.com}")
    private String baseUrl;

    public SitemapController(SitemapService sitemapService) {
        this.sitemapService = sitemapService;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(
        summary = "Generate main sitemap",
        description = "Returns the main sitemap XML with static pages and property listings. " +
                     "Automatically generated for optimal SEO."
    )
    public ResponseEntity<String> getMainSitemap() {
        String sitemap = sitemapService.generateMainSitemap(baseUrl);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setCacheControl("public, max-age=3600"); // Cache 1 heure
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(sitemap);
    }

    @GetMapping(value = "/sitemap-properties.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(
        summary = "Generate properties sitemap",
        description = "Returns the properties sitemap XML with all published properties. " +
                     "Supports pagination for large datasets."
    )
    public ResponseEntity<String> getPropertiesSitemap(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50000") int size) {
        
        String sitemap = sitemapService.generatePropertiesSitemap(baseUrl, page, size);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setCacheControl("public, max-age=3600"); // Cache 1 heure
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(sitemap);
    }

    @GetMapping(value = "/sitemap-index.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(
        summary = "Generate sitemap index",
        description = "Returns the sitemap index XML if there are more than 50k properties. " +
                     "Used for large sites to organize multiple sitemaps."
    )
    public ResponseEntity<String> getSitemapIndex() {
        String sitemapIndex = sitemapService.generateSitemapIndex(baseUrl);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setCacheControl("public, max-age=3600"); // Cache 1 heure
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(sitemapIndex);
    }

    @GetMapping(value = "/sitemap-blog.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(
        summary = "Generate blog sitemap",
        description = "Returns the blog posts sitemap XML with all published blog articles. " +
                     "Optimized for SEO with SEO-friendly slugs."
    )
    public ResponseEntity<String> getBlogSitemap() {
        String sitemap = sitemapService.generateBlogSitemap(baseUrl);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setCacheControl("public, max-age=3600"); // Cache 1 heure
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(sitemap);
    }
}

