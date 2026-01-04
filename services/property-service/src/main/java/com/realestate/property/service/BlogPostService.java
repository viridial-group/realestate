package com.realestate.property.service;

import com.realestate.property.entity.BlogPost;
import com.realestate.property.repository.BlogPostRepository;
import com.realestate.property.util.SlugGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des articles de blog
 * Optimisé pour le SEO
 */
@Service
public class BlogPostService {

    private static final Logger logger = LoggerFactory.getLogger(BlogPostService.class);

    private final BlogPostRepository blogPostRepository;
    private final SlugGenerator slugGenerator;

    public BlogPostService(BlogPostRepository blogPostRepository, SlugGenerator slugGenerator) {
        this.blogPostRepository = blogPostRepository;
        this.slugGenerator = slugGenerator;
    }

    /**
     * Récupère tous les articles publiés avec pagination
     */
    public Page<BlogPost> getPublishedPosts(Pageable pageable) {
        return blogPostRepository.findPublishedPosts(pageable, LocalDateTime.now());
    }

    /**
     * Récupère un article publié par son slug
     */
    public Optional<BlogPost> getPublishedPostBySlug(String slug) {
        Optional<BlogPost> post = blogPostRepository.findBySlug(slug);
        if (post.isPresent() && post.get().getStatus().equals("PUBLISHED") 
            && post.get().getPublishedAt() != null 
            && post.get().getPublishedAt().isBefore(LocalDateTime.now())) {
            // Incrémenter le compteur de vues
            incrementViewCount(post.get().getId());
            return post;
        }
        return Optional.empty();
    }

    /**
     * Récupère les articles récents (pour la page d'accueil)
     */
    public List<BlogPost> getRecentPublishedPosts(int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return blogPostRepository.findRecentPublishedPosts(LocalDateTime.now(), pageable);
    }

    /**
     * Récupère les articles par catégorie
     */
    public Page<BlogPost> getPublishedPostsByCategory(String category, Pageable pageable) {
        return blogPostRepository.findPublishedPostsByCategory(category, pageable, LocalDateTime.now());
    }

    /**
     * Récupère les articles par tag
     */
    public Page<BlogPost> getPublishedPostsByTag(String tag, Pageable pageable) {
        return blogPostRepository.findPublishedPostsByTag(tag, pageable, LocalDateTime.now());
    }

    /**
     * Recherche dans les articles publiés
     */
    public Page<BlogPost> searchPublishedPosts(String search, Pageable pageable) {
        return blogPostRepository.searchPublishedPosts(search, pageable, LocalDateTime.now());
    }

    /**
     * Récupère toutes les catégories distinctes
     */
    public List<String> getCategories() {
        return blogPostRepository.findDistinctCategories();
    }

    /**
     * Récupère tous les articles publiés (pour sitemap)
     */
    public List<BlogPost> getAllPublishedPosts() {
        return blogPostRepository.findAllPublishedPosts(LocalDateTime.now());
    }

    /**
     * Crée un nouvel article de blog
     */
    @Transactional
    public BlogPost createBlogPost(BlogPost blogPost) {
        // Générer le slug si non fourni
        if (blogPost.getSlug() == null || blogPost.getSlug().isEmpty()) {
            String slug = slugGenerator.generateSlug(blogPost.getTitle());
            // Vérifier l'unicité
            String uniqueSlug = ensureUniqueSlug(slug);
            blogPost.setSlug(uniqueSlug);
        }

        // Si publié, définir la date de publication
        if ("PUBLISHED".equals(blogPost.getStatus()) && blogPost.getPublishedAt() == null) {
            blogPost.setPublishedAt(LocalDateTime.now());
        }

        return blogPostRepository.save(blogPost);
    }

    /**
     * Met à jour un article de blog
     */
    @Transactional
    public BlogPost updateBlogPost(Long id, BlogPost updatedPost) {
        BlogPost existing = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found: " + id));

        // Mettre à jour les champs
        existing.setTitle(updatedPost.getTitle());
        existing.setExcerpt(updatedPost.getExcerpt());
        existing.setContent(updatedPost.getContent());
        existing.setCategory(updatedPost.getCategory());
        existing.setTags(updatedPost.getTags());
        existing.setFeaturedImage(updatedPost.getFeaturedImage());
        existing.setMetaDescription(updatedPost.getMetaDescription());
        existing.setMetaKeywords(updatedPost.getMetaKeywords());
        existing.setOgImage(updatedPost.getOgImage());
        existing.setStatus(updatedPost.getStatus());

        // Si le statut passe à PUBLISHED et qu'il n'y a pas de date de publication
        if ("PUBLISHED".equals(updatedPost.getStatus()) && existing.getPublishedAt() == null) {
            existing.setPublishedAt(LocalDateTime.now());
        }

        // Mettre à jour le slug si le titre a changé
        if (!existing.getTitle().equals(updatedPost.getTitle())) {
            String newSlug = slugGenerator.generateSlug(updatedPost.getTitle());
            existing.setSlug(ensureUniqueSlug(newSlug, id));
        }

        return blogPostRepository.save(existing);
    }

    /**
     * Incrémente le compteur de vues
     */
    @Transactional
    public void incrementViewCount(Long id) {
        blogPostRepository.findById(id).ifPresent(post -> {
            post.setViewCount((post.getViewCount() != null ? post.getViewCount() : 0L) + 1);
            blogPostRepository.save(post);
        });
    }

    /**
     * Assure l'unicité du slug
     */
    private String ensureUniqueSlug(String slug) {
        return ensureUniqueSlug(slug, null);
    }

    private String ensureUniqueSlug(String slug, Long excludeId) {
        String baseSlug = slug;
        int counter = 1;
        
        while (true) {
            Optional<BlogPost> existing = blogPostRepository.findBySlug(slug);
            if (existing.isEmpty() || (excludeId != null && existing.get().getId().equals(excludeId))) {
                return slug;
            }
            slug = baseSlug + "-" + counter;
            counter++;
        }
    }
}

