package com.realestate.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entité pour les articles de blog
 * Optimisé pour le SEO avec slug, meta tags, etc.
 */
@Entity
@Table(name = "blog_posts", indexes = {
    @Index(name = "idx_blog_slug", columnList = "slug", unique = true),
    @Index(name = "idx_blog_status", columnList = "status"),
    @Index(name = "idx_blog_published_at", columnList = "published_at"),
    @Index(name = "idx_blog_category", columnList = "category")
})
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @Size(max = 500)
    private String excerpt; // Résumé court pour les listes

    @Column(columnDefinition = "TEXT")
    private String content; // Contenu complet en Markdown ou HTML

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String slug; // URL SEO-friendly

    @Size(max = 100)
    private String category; // Ex: "Guide", "Actualités", "Conseils"

    @Size(max = 500)
    private String tags; // Tags séparés par des virgules

    @Size(max = 500)
    private String featuredImage; // URL de l'image principale

    // SEO Meta Tags
    @Size(max = 500)
    @Column(name = "meta_description")
    private String metaDescription;

    @Size(max = 500)
    @Column(name = "meta_keywords")
    private String metaKeywords;

    @Size(max = 500)
    @Column(name = "og_image")
    private String ogImage; // Image pour Open Graph

    // Statut de publication
    @Size(max = 50)
    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT, PUBLISHED, ARCHIVED

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // Auteur
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Size(max = 255)
    @Column(name = "author_name")
    private String authorName; // Nom de l'auteur (pour éviter les jointures)

    // Organisation (optionnel, pour multi-tenant)
    @Column(name = "organization_id")
    private Long organizationId;

    // Statistiques
    @Column(name = "view_count")
    private Long viewCount = 0L;

    // Dates
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public String getOgImage() {
        return ogImage;
    }

    public void setOgImage(String ogImage) {
        this.ogImage = ogImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

