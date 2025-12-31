package com.realestate.common.repository.elasticsearch;

import com.realestate.common.document.PropertyDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository Elasticsearch pour les Properties
 */
@Repository
public interface PropertyDocumentRepository extends ElasticsearchRepository<PropertyDocument, Long> {

    /**
     * Recherche par organisation
     */
    Page<PropertyDocument> findByOrganizationId(Long organizationId, Pageable pageable);

    /**
     * Recherche full-text dans title et description
     */
    Page<PropertyDocument> findByTitleContainingOrDescriptionContaining(
            String title, String description, Pageable pageable);

    /**
     * Recherche par ville
     */
    List<PropertyDocument> findByCity(String city);

    /**
     * Recherche par type
     */
    List<PropertyDocument> findByType(String type);

    /**
     * Recherche par statut
     */
    List<PropertyDocument> findByStatus(String status);

    /**
     * Recherche par plage de prix
     */
    List<PropertyDocument> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Recherche par organisation et ville
     */
    Page<PropertyDocument> findByOrganizationIdAndCity(
            Long organizationId, String city, Pageable pageable);

    /**
     * Recherche par organisation et type
     */
    Page<PropertyDocument> findByOrganizationIdAndType(
            Long organizationId, String type, Pageable pageable);

    /**
     * Recherche par organisation et statut
     */
    Page<PropertyDocument> findByOrganizationIdAndStatus(
            Long organizationId, String status, Pageable pageable);
}

