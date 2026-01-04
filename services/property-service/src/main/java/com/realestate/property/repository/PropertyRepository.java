package com.realestate.property.repository;

import com.realestate.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    Optional<Property> findByReference(String reference);

    Optional<Property> findBySlug(String slug);

    List<Property> findByOrganizationId(Long organizationId);

    @Query("SELECT p FROM Property p WHERE p.organizationId = :organizationId AND p.active = true")
    List<Property> findActiveByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT p FROM Property p WHERE p.assignedUserId = :userId AND p.active = true")
    List<Property> findActiveByAssignedUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Property p WHERE p.teamId = :teamId AND p.active = true")
    List<Property> findActiveByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT p FROM Property p WHERE p.status = :status AND p.active = true")
    List<Property> findActiveByStatus(@Param("status") String status);

    @Query("SELECT p FROM Property p WHERE p.type = :type AND p.active = true")
    List<Property> findActiveByType(@Param("type") String type);

    @Query("SELECT p FROM Property p WHERE p.city = :city AND p.active = true")
    List<Property> findActiveByCity(@Param("city") String city);

    @Query("SELECT p FROM Property p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = true")
    List<Property> findActiveByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT p FROM Property p WHERE p.surface BETWEEN :minSurface AND :maxSurface AND p.active = true")
    List<Property> findActiveBySurfaceRange(
            @Param("minSurface") BigDecimal minSurface,
            @Param("maxSurface") BigDecimal maxSurface
    );

    @Query("SELECT p FROM Property p WHERE p.organizationId = :organizationId AND p.status = :status AND p.active = true")
    List<Property> findActiveByOrganizationIdAndStatus(
            @Param("organizationId") Long organizationId,
            @Param("status") String status
    );

    /**
     * Récupère les villes distinctes avec des propriétés publiées/disponibles
     * Optimisé pour l'autocomplete
     */
    @Query("SELECT DISTINCT p.city FROM Property p WHERE (p.status = 'PUBLISHED' OR p.status = 'AVAILABLE') AND p.active = true AND p.city IS NOT NULL AND p.city != '' ORDER BY p.city")
    List<String> findDistinctCitiesForPublishedProperties();

    /**
     * Récupère les villes distinctes avec filtre de recherche
     */
    @Query("SELECT DISTINCT p.city FROM Property p WHERE (p.status = 'PUBLISHED' OR p.status = 'AVAILABLE') AND p.active = true AND p.city IS NOT NULL AND p.city != '' AND LOWER(p.city) LIKE LOWER(CONCAT('%', :search, '%')) ORDER BY p.city")
    List<String> findDistinctCitiesForPublishedPropertiesContaining(@Param("search") String search);

    /**
     * Récupère les types distincts avec des propriétés publiées/disponibles
     */
    @Query("SELECT DISTINCT p.type FROM Property p WHERE (p.status = 'PUBLISHED' OR p.status = 'AVAILABLE') AND p.active = true AND p.type IS NOT NULL AND p.type != '' ORDER BY p.type")
    List<String> findDistinctTypesForPublishedProperties();

    /**
     * Récupère les types distincts avec filtre de recherche
     */
    @Query("SELECT DISTINCT p.type FROM Property p WHERE (p.status = 'PUBLISHED' OR p.status = 'AVAILABLE') AND p.active = true AND p.type IS NOT NULL AND p.type != '' AND LOWER(p.type) LIKE LOWER(CONCAT('%', :search, '%')) ORDER BY p.type")
    List<String> findDistinctTypesForPublishedPropertiesContaining(@Param("search") String search);

    /**
     * Récupère les quartiers/adresses distincts avec filtre de recherche
     */
    @Query("SELECT DISTINCT p.address FROM Property p WHERE (p.status = 'PUBLISHED' OR p.status = 'AVAILABLE') AND p.active = true AND p.address IS NOT NULL AND p.address != '' AND LOWER(p.address) LIKE LOWER(CONCAT('%', :search, '%')) ORDER BY p.address")
    List<String> findDistinctAddressesForPublishedPropertiesContaining(@Param("search") String search);

    /**
     * Récupère les titres de propriétés correspondant à la recherche
     */
    @Query("SELECT DISTINCT p.title FROM Property p WHERE (p.status = 'PUBLISHED' OR p.status = 'AVAILABLE') AND p.active = true AND p.title IS NOT NULL AND p.title != '' AND LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) ORDER BY p.title")
    List<String> findDistinctTitlesForPublishedPropertiesContaining(@Param("search") String search);

    /**
     * Récupère les propriétés par statuts (pour sitemap)
     */
    @Query("SELECT p FROM Property p WHERE p.status IN :statuses AND p.active = true")
    org.springframework.data.domain.Page<Property> findByStatusIn(
            @Param("statuses") List<String> statuses,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * Compte les propriétés par statuts (pour sitemap)
     */
    @Query("SELECT COUNT(p) FROM Property p WHERE p.status IN :statuses AND p.active = true")
    long countByStatusIn(@Param("statuses") List<String> statuses);

    /**
     * Compte les propriétés actives d'une organisation
     */
    @Query("SELECT COUNT(p) FROM Property p WHERE p.organizationId = :organizationId AND p.active = true")
    Long countByOrganizationId(@Param("organizationId") Long organizationId);
}

