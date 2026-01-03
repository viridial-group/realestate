package com.realestate.property.service;

import com.realestate.property.dto.PagedPropertyResponse;
import com.realestate.property.dto.PropertyDTO;
import com.realestate.property.entity.Property;
import com.realestate.property.mapper.PropertyMapper;
import com.realestate.property.repository.PropertyRepository;
import com.realestate.property.specification.PropertySpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service optimisé pour les requêtes publiques de propriétés
 * 
 * Stratégie de performance :
 * 1. Utilise Elasticsearch si disponible (recherche full-text, filtres complexes)
 * 2. Fallback sur PostgreSQL avec JPA Specifications (filtres simples)
 * 3. Cache Redis pour les résultats fréquents
 * 4. Pagination optimisée
 */
@Service
public class PublicPropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PublicPropertyService.class);
    
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    
    // Optionnel : Elasticsearch si disponible
    @Autowired(required = false)
    private PropertySearchService propertySearchService;

    public PublicPropertyService(
            PropertyRepository propertyRepository,
            PropertyMapper propertyMapper) {
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
    }

    /**
     * Récupère les propriétés publiées avec optimisations de performance
     * 
     * Stratégie :
     * - Si recherche textuelle complexe ET Elasticsearch disponible → utilise Elasticsearch
     * - Sinon → utilise PostgreSQL avec JPA Specifications (plus rapide pour filtres simples)
     * - Cache Redis avec PagedPropertyResponse (sérialisable)
     */
    @Cacheable(value = "publicProperties", key = "#root.method.name + '_' + #type + '_' + #city + '_' + #minPrice + '_' + #maxPrice + '_' + #page + '_' + #size + '_' + #search + '_' + #sortBy + '_' + (#createdAfter != null ? #createdAfter : 'null')")
    @Transactional(readOnly = true)
    public PagedPropertyResponse getPublishedProperties(
            String type,
            String city,
            String country,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minSurface,
            BigDecimal maxSurface,
            Integer bedrooms,
            Integer bathrooms,
            String search,
            String sortBy,
            String createdAfter,
            int page,
            int size) {
        
        long startTime = System.currentTimeMillis();
        
            try {
                // Stratégie 1 : Si recherche textuelle ET Elasticsearch disponible → utilise Elasticsearch
                if (search != null && !search.trim().isEmpty() && propertySearchService != null) {
                    logger.debug("Using Elasticsearch for text search: {}", search);
                    return getPropertiesFromElasticsearch(
                            type, city, country, minPrice, maxPrice, minSurface, maxSurface,
                            bedrooms, bathrooms, search, sortBy, createdAfter, page, size);
                }

                // Stratégie 2 : PostgreSQL avec JPA Specifications (plus rapide pour filtres simples)
                logger.debug("Using PostgreSQL with JPA Specifications");
                return getPropertiesFromDatabase(
                        type, city, country, minPrice, maxPrice, minSurface, maxSurface,
                        bedrooms, bathrooms, search, sortBy, createdAfter, page, size);
                    
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Public property search completed in {}ms", duration);
        }
    }

        /**
         * Récupère depuis Elasticsearch (si disponible)
         */
        private PagedPropertyResponse getPropertiesFromElasticsearch(
                String type, String city, String country,
                BigDecimal minPrice, BigDecimal maxPrice,
                BigDecimal minSurface, BigDecimal maxSurface,
                Integer bedrooms, Integer bathrooms,
                String search, String sortBy, String createdAfter, int page, int size) {

            // TODO: Implémenter la recherche Elasticsearch avec tous les filtres
            // Pour l'instant, fallback sur PostgreSQL
            logger.warn("Elasticsearch search not fully implemented, falling back to PostgreSQL");
            return getPropertiesFromDatabase(
                    type, city, country, minPrice, maxPrice, minSurface, maxSurface,
                    bedrooms, bathrooms, search, sortBy, createdAfter, page, size);
        }

    /**
     * Récupère depuis PostgreSQL avec JPA Specifications (optimisé)
     */
    private PagedPropertyResponse getPropertiesFromDatabase(
            String type, String city, String country,
            BigDecimal minPrice, BigDecimal maxPrice,
            BigDecimal minSurface, BigDecimal maxSurface,
            Integer bedrooms, Integer bathrooms,
            String search, String sortBy, String createdAfter, int page, int size) {
        
        // Construire la spécification avec tous les filtres
        Specification<Property> spec = Specification.where(
                PropertySpecification.hasStatus("PUBLISHED")
                        .or(PropertySpecification.hasStatus("AVAILABLE"))
        );
        
        if (type != null && !type.isEmpty()) {
            spec = spec.and(PropertySpecification.hasType(type));
        }
        
        if (city != null && !city.isEmpty()) {
            spec = spec.and(PropertySpecification.hasCity(city));
        }
        
        if (country != null && !country.isEmpty()) {
            spec = spec.and(PropertySpecification.hasCountry(country));
        }
        
        if (minPrice != null) {
            spec = spec.and(PropertySpecification.hasMinPrice(minPrice));
        }
        
        if (maxPrice != null) {
            spec = spec.and(PropertySpecification.hasMaxPrice(maxPrice));
        }
        
        if (minSurface != null) {
            spec = spec.and(PropertySpecification.hasMinSurface(minSurface));
        }
        
        if (maxSurface != null) {
            spec = spec.and(PropertySpecification.hasMaxSurface(maxSurface));
        }
        
        if (bedrooms != null) {
            spec = spec.and(PropertySpecification.hasBedrooms(bedrooms));
        }
        
        if (bathrooms != null) {
            spec = spec.and(PropertySpecification.hasBathrooms(bathrooms));
        }
        
        if (search != null && !search.trim().isEmpty()) {
            // Utiliser le parseur de requête style Google
            com.realestate.property.util.SearchQueryParser.ParsedQuery parsedQuery = 
                    com.realestate.property.util.SearchQueryParser.parse(search);
            
            if (!parsedQuery.isEmpty()) {
                // Utiliser la recherche avancée
                spec = spec.and(PropertySpecification.advancedTextSearch(
                        parsedQuery.getIncludeTerms(),
                        parsedQuery.getExactPhrases(),
                        parsedQuery.getExcludeTerms()
                ));
            } else {
                // Si le parseur ne trouve rien (tous les mots sont ignorés), 
                // utiliser une recherche simple sur la requête complète
                spec = spec.and(PropertySpecification.searchByText(search));
            }
        }

        // Filtre par date de création
        if (createdAfter != null && !createdAfter.trim().isEmpty()) {
            try {
                java.time.LocalDateTime date = java.time.LocalDateTime.parse(createdAfter);
                spec = spec.and(PropertySpecification.createdAfter(date));
            } catch (Exception e) {
                logger.warn("Invalid createdAfter date format: {}", createdAfter, e);
            }
        }

        // Déterminer le tri selon le paramètre sortBy
        Sort sort = getSortFromParameter(sortBy);
        
        // Pagination optimisée avec tri
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Property> propertyPage = propertyRepository.findAll(spec, pageable);
        
        // Convertir Page<Property> en List<PropertyDTO>
        List<PropertyDTO> content = propertyPage.getContent().stream()
                .map(propertyMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
        
        // Créer la réponse paginée sérialisable
        return new PagedPropertyResponse(
                content,
                propertyPage.getNumber(),
                propertyPage.getTotalPages(),
                propertyPage.getTotalElements(),
                propertyPage.getSize(),
                propertyPage.isFirst(),
                propertyPage.isLast()
        );
    }
    
    /**
     * Convertit le paramètre sortBy en Sort Spring Data
     * Format attendu: "field-direction" (ex: "price-asc", "surface-desc")
     */
    private Sort getSortFromParameter(String sortBy) {
        if (sortBy == null || sortBy.isEmpty() || "default".equals(sortBy)) {
            // Par défaut: tri par date de création (plus récentes en premier)
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        
        String[] parts = sortBy.split("-");
        if (parts.length != 2) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        
        String field = parts[0];
        String direction = parts[1].toLowerCase();
        
        // Mapper les champs frontend vers les champs entity
        String entityField = mapFieldToEntity(field);
        Sort.Direction sortDirection = "asc".equals(direction) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        
        return Sort.by(sortDirection, entityField);
    }
    
    /**
     * Mappe les champs du frontend vers les champs de l'entité Property
     */
    private String mapFieldToEntity(String field) {
        switch (field.toLowerCase()) {
            case "price":
                return "price";
            case "surface":
                return "surface";
            case "created":
            case "createdat":
                return "createdAt";
            case "updated":
            case "updatedat":
                return "updatedAt";
            default:
                return "createdAt"; // Par défaut
        }
    }

    /**
     * Récupère la liste des villes disponibles (pour autocomplete)
     * Filtre uniquement les villes avec des propriétés publiées/disponibles
     * Utilise une requête optimisée pour récupérer uniquement les villes distinctes
     */
    @Cacheable(value = "availableCities", key = "#search != null ? #search : 'all'")
    @Transactional(readOnly = true)
    public List<String> getAvailableCities(String search) {
        if (search != null && !search.trim().isEmpty()) {
            List<String> cities = propertyRepository.findDistinctCitiesForPublishedPropertiesContaining(search.trim());
            return cities.stream().limit(50).collect(java.util.stream.Collectors.toList());
        } else {
            List<String> cities = propertyRepository.findDistinctCitiesForPublishedProperties();
            return cities.stream().limit(50).collect(java.util.stream.Collectors.toList());
        }
    }
    
    /**
     * Récupère une propriété publiée par ID (avec cache)
     */
    @Cacheable(value = "publicProperty", key = "#id")
    @Transactional(readOnly = true)
    public PropertyDTO getPublishedPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new com.realestate.common.exception.ResourceNotFoundException("Property", id));
        
        // Vérifier que la propriété est publiée
        if (!"PUBLISHED".equals(property.getStatus()) && !"AVAILABLE".equals(property.getStatus())) {
            throw new com.realestate.common.exception.ResourceNotFoundException("Property", id);
        }
        
        return propertyMapper.toDTO(property);
    }

    /**
     * Récupère une propriété publiée par référence (avec cache)
     */
    @Cacheable(value = "publicProperty", key = "#reference")
    @Transactional(readOnly = true)
    public PropertyDTO getPublishedPropertyByReference(String reference) {
        Property property = propertyRepository.findByReference(reference)
                .orElseThrow(() -> new com.realestate.common.exception.ResourceNotFoundException("Property", reference));
        
        // Vérifier que la propriété est publiée
        if (!"PUBLISHED".equals(property.getStatus()) && !"AVAILABLE".equals(property.getStatus())) {
            throw new com.realestate.common.exception.ResourceNotFoundException("Property", reference);
        }
        
        return propertyMapper.toDTO(property);
    }
}

