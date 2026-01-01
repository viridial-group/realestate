package com.realestate.property.service;

import com.realestate.common.document.PropertyDocument;
import com.realestate.common.repository.elasticsearch.PropertyDocumentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service de recherche Elasticsearch pour les Properties
 * Conditionnel : ne sera créé que si PropertyDocumentRepository est disponible
 */
@Service
@ConditionalOnBean(PropertyDocumentRepository.class)
public class PropertySearchService {

    private final PropertyDocumentRepository propertyDocumentRepository;

    public PropertySearchService(PropertyDocumentRepository propertyDocumentRepository) {
        this.propertyDocumentRepository = propertyDocumentRepository;
    }

    /**
     * Recherche full-text dans title et description
     */
    public Page<PropertyDocument> searchByText(String searchTerm, Pageable pageable) {
        return propertyDocumentRepository.findByTitleContainingOrDescriptionContaining(
                searchTerm, searchTerm, pageable);
    }

    /**
     * Recherche par organisation
     */
    public Page<PropertyDocument> searchByOrganization(Long organizationId, Pageable pageable) {
        return propertyDocumentRepository.findByOrganizationId(organizationId, pageable);
    }

    /**
     * Recherche par ville
     */
    public List<PropertyDocument> searchByCity(String city) {
        return propertyDocumentRepository.findByCity(city);
    }

    /**
     * Recherche par type
     */
    public List<PropertyDocument> searchByType(String type) {
        return propertyDocumentRepository.findByType(type);
    }

    /**
     * Recherche par statut
     */
    public List<PropertyDocument> searchByStatus(String status) {
        return propertyDocumentRepository.findByStatus(status);
    }

    /**
     * Recherche par plage de prix
     */
    public List<PropertyDocument> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return propertyDocumentRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Recherche combinée : organisation + ville
     */
    public Page<PropertyDocument> searchByOrganizationAndCity(
            Long organizationId, String city, Pageable pageable) {
        return propertyDocumentRepository.findByOrganizationIdAndCity(organizationId, city, pageable);
    }

    /**
     * Recherche combinée : organisation + type
     */
    public Page<PropertyDocument> searchByOrganizationAndType(
            Long organizationId, String type, Pageable pageable) {
        return propertyDocumentRepository.findByOrganizationIdAndType(organizationId, type, pageable);
    }

    /**
     * Recherche combinée : organisation + statut
     */
    public Page<PropertyDocument> searchByOrganizationAndStatus(
            Long organizationId, String status, Pageable pageable) {
        return propertyDocumentRepository.findByOrganizationIdAndStatus(organizationId, status, pageable);
    }
}

