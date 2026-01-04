package com.realestate.property.repository;

import com.realestate.property.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    /**
     * Trouve un article par son slug (pour URLs SEO-friendly)
     */
    Optional<BlogPost> findBySlug(String slug);

    /**
     * Trouve tous les articles publiés, triés par date de publication décroissante
     */
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' AND b.publishedAt <= :now ORDER BY b.publishedAt DESC")
    Page<BlogPost> findPublishedPosts(Pageable pageable, @Param("now") LocalDateTime now);

    /**
     * Trouve les articles publiés par catégorie
     */
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' AND b.category = :category AND b.publishedAt <= :now ORDER BY b.publishedAt DESC")
    Page<BlogPost> findPublishedPostsByCategory(
            @Param("category") String category,
            Pageable pageable,
            @Param("now") LocalDateTime now
    );

    /**
     * Trouve les articles publiés récents (pour la page d'accueil)
     */
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' AND b.publishedAt <= :now ORDER BY b.publishedAt DESC")
    List<BlogPost> findRecentPublishedPosts(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * Trouve les articles par tag
     */
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' AND b.publishedAt <= :now AND b.tags LIKE %:tag% ORDER BY b.publishedAt DESC")
    Page<BlogPost> findPublishedPostsByTag(
            @Param("tag") String tag,
            Pageable pageable,
            @Param("now") LocalDateTime now
    );

    /**
     * Recherche dans les articles publiés (titre, contenu, excerpt)
     */
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' AND b.publishedAt <= :now " +
           "AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(b.excerpt) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(b.content) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY b.publishedAt DESC")
    Page<BlogPost> searchPublishedPosts(
            @Param("search") String search,
            Pageable pageable,
            @Param("now") LocalDateTime now
    );

    /**
     * Trouve tous les articles publiés (pour sitemap)
     */
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' AND b.publishedAt <= :now")
    List<BlogPost> findAllPublishedPosts(@Param("now") LocalDateTime now);

    /**
     * Trouve les catégories distinctes
     */
    @Query("SELECT DISTINCT b.category FROM BlogPost b WHERE b.status = 'PUBLISHED' AND b.category IS NOT NULL AND b.category != ''")
    List<String> findDistinctCategories();

    /**
     * Trouve les articles par organisation (multi-tenant)
     */
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' AND b.organizationId = :organizationId AND b.publishedAt <= :now ORDER BY b.publishedAt DESC")
    Page<BlogPost> findPublishedPostsByOrganization(
            @Param("organizationId") Long organizationId,
            Pageable pageable,
            @Param("now") LocalDateTime now
    );
}

