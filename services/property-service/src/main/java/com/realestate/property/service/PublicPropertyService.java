package com.realestate.property.service;

import com.realestate.property.dto.PagedPropertyResponse;
import com.realestate.property.dto.PropertyDTO;
import com.realestate.property.dto.SearchSuggestionsDTO;
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
    @Cacheable(value = "publicProperties", key = "#root.method.name + '_' + (#organizationId != null ? #organizationId : 'null') + '_' + #type + '_' + #city + '_' + #minPrice + '_' + #maxPrice + '_' + #page + '_' + #size + '_' + #search + '_' + #sortBy + '_' + #transactionType + '_' + (#createdAfter != null ? #createdAfter : 'null')")
    @Transactional(readOnly = true)
    public PagedPropertyResponse getPublishedProperties(
            Long organizationId,
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
            String transactionType,
            String createdAfter,
            int page,
            int size) {
        
        long startTime = System.currentTimeMillis();
        
            try {
                // Stratégie 1 : Si recherche textuelle ET Elasticsearch disponible → utilise Elasticsearch
                if (search != null && !search.trim().isEmpty() && propertySearchService != null) {
                    logger.debug("Using Elasticsearch for text search: {}", search);
                    return getPropertiesFromElasticsearch(
                            organizationId, type, city, country, minPrice, maxPrice, minSurface, maxSurface,
                            bedrooms, bathrooms, search, sortBy, transactionType, createdAfter, page, size);
                }

                // Stratégie 2 : PostgreSQL avec JPA Specifications (plus rapide pour filtres simples)
                logger.debug("Using PostgreSQL with JPA Specifications");
                return getPropertiesFromDatabase(
                        organizationId, type, city, country, minPrice, maxPrice, minSurface, maxSurface,
                        bedrooms, bathrooms, search, sortBy, transactionType, createdAfter, page, size);
                    
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Public property search completed in {}ms", duration);
        }
    }

        /**
         * Récupère depuis Elasticsearch (si disponible)
         */
        private PagedPropertyResponse getPropertiesFromElasticsearch(
                Long organizationId,
                String type, String city, String country,
                BigDecimal minPrice, BigDecimal maxPrice,
                BigDecimal minSurface, BigDecimal maxSurface,
                Integer bedrooms, Integer bathrooms,
                String search, String sortBy, String transactionType, String createdAfter, int page, int size) {

            // TODO: Implémenter la recherche Elasticsearch avec tous les filtres
            // Pour l'instant, fallback sur PostgreSQL
            logger.warn("Elasticsearch search not fully implemented, falling back to PostgreSQL");
            return getPropertiesFromDatabase(
                    organizationId, type, city, country, minPrice, maxPrice, minSurface, maxSurface,
                    bedrooms, bathrooms, search, sortBy, transactionType, createdAfter, page, size);
        }

    /**
     * Récupère depuis PostgreSQL avec JPA Specifications (optimisé)
     */
    private PagedPropertyResponse getPropertiesFromDatabase(
            Long organizationId,
            String type, String city, String country,
            BigDecimal minPrice, BigDecimal maxPrice,
            BigDecimal minSurface, BigDecimal maxSurface,
            Integer bedrooms, Integer bathrooms,
            String search, String sortBy, String transactionType, String createdAfter, int page, int size) {
        
        // Construire la spécification avec tous les filtres
        Specification<Property> spec = Specification.where(
                PropertySpecification.hasStatus("PUBLISHED")
                        .or(PropertySpecification.hasStatus("AVAILABLE"))
        );
        
        // Filtre par organisation
        if (organizationId != null) {
            spec = spec.and(PropertySpecification.hasOrganization(organizationId));
        }
        
        if (type != null && !type.isEmpty()) {
            spec = spec.and(PropertySpecification.hasType(type));
        }
        
        // Filtre par type de transaction (RENT, SALE)
        if (transactionType != null && !transactionType.isEmpty()) {
            // Convertir Location/Vente en RENT/SALE
            String transactionTypeValue = transactionType.toUpperCase();
            if ("LOCATION".equals(transactionTypeValue)) {
                transactionTypeValue = "RENT";
            } else if ("VENTE".equals(transactionTypeValue)) {
                transactionTypeValue = "SALE";
            }
            spec = spec.and(PropertySpecification.hasTransactionType(transactionTypeValue));
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
     * Récupère des suggestions complètes de recherche basées sur la requête
     * Inclut villes, types, adresses, titres et recherches populaires
     * Amélioré avec support de limit, includePopular, includeTrending
     */
    @Cacheable(value = "searchSuggestions", 
               key = "#search != null ? #search : 'empty' + '-' + (#limit != null ? #limit : 10) + '-' + (#includePopular != null ? #includePopular : true) + '-' + (#includeTrending != null ? #includeTrending : true)")
    @Transactional(readOnly = true)
    public SearchSuggestionsDTO getSearchSuggestions(
            String search, 
            Integer limit, 
            Boolean includePopular, 
            Boolean includeTrending) {
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim().toLowerCase() : null;
        int limitValue = (limit != null && limit > 0 && limit <= 50) ? limit : 10;
        boolean includePopularValue = includePopular != null ? includePopular : true;
        // includeTrending réservé pour usage futur (tendances de recherche)
        // boolean includeTrendingValue = includeTrending != null ? includeTrending : true;
        
        List<String> cities = List.of();
        List<String> types = List.of();
        List<String> addresses = List.of();
        List<String> titles = List.of();
        List<String> popularSearches = List.of();

        if (searchTerm != null && searchTerm.length() >= 2) {
            // Extraire les mots significatifs de la requête (ignorer "a", "à", "de", etc.)
            String[] words = searchTerm.split("\\s+");
            List<String> significantWords = new java.util.ArrayList<>();
            for (String word : words) {
                if (word.length() >= 3 && !isStopWord(word)) {
                    significantWords.add(word);
                }
            }
            
            // Récupérer les villes correspondantes (chercher avec chaque mot significatif)
            cities = new java.util.ArrayList<>();
            for (String word : significantWords) {
                List<String> foundCities = propertyRepository.findDistinctCitiesForPublishedPropertiesContaining(word)
                        .stream().limit(limitValue).collect(java.util.stream.Collectors.toList());
                for (String city : foundCities) {
                    if (!cities.contains(city)) {
                        cities.add(city);
                    }
                }
            }
            // Chercher aussi avec la requête complète (pour gérer "a paris", "à paris", etc.)
            List<String> foundCitiesFromFullQuery = propertyRepository.findDistinctCitiesForPublishedPropertiesContaining(searchTerm)
                    .stream().limit(limitValue).collect(java.util.stream.Collectors.toList());
            for (String city : foundCitiesFromFullQuery) {
                if (!cities.contains(city)) {
                    cities.add(city);
                }
            }
            // Chercher aussi avec des patterns comme "a paris", "à paris"
            if (searchTerm.contains(" a ") || searchTerm.contains(" à ")) {
                String[] parts = searchTerm.split("\\s+(a|à)\\s+");
                if (parts.length > 1) {
                    String potentialCity = parts[parts.length - 1].trim();
                    if (potentialCity.length() >= 3) {
                        List<String> foundCities = propertyRepository.findDistinctCitiesForPublishedPropertiesContaining(potentialCity)
                                .stream().limit(10).collect(java.util.stream.Collectors.toList());
                        for (String city : foundCities) {
                            if (!cities.contains(city)) {
                                cities.add(city);
                            }
                        }
                    }
                }
            }

            // Récupérer les types correspondants
            types = new java.util.ArrayList<>();
            for (String word : significantWords) {
                List<String> foundTypes = propertyRepository.findDistinctTypesForPublishedPropertiesContaining(word)
                        .stream().limit(limitValue).collect(java.util.stream.Collectors.toList());
                for (String type : foundTypes) {
                    if (!types.contains(type)) {
                        types.add(type);
                    }
                }
            }
            // Si aucun type trouvé, essayer avec la requête complète
            if (types.isEmpty()) {
                types = propertyRepository.findDistinctTypesForPublishedPropertiesContaining(searchTerm)
                        .stream().limit(limitValue).collect(java.util.stream.Collectors.toList());
            }

            // Récupérer les adresses correspondantes
            addresses = propertyRepository.findDistinctAddressesForPublishedPropertiesContaining(searchTerm)
                    .stream().limit(limitValue).collect(java.util.stream.Collectors.toList());

            // Récupérer les titres correspondants
            titles = propertyRepository.findDistinctTitlesForPublishedPropertiesContaining(searchTerm)
                    .stream().limit(limitValue).collect(java.util.stream.Collectors.toList());

            // Générer des recherches populaires basées sur les combinaisons (si activé)
            if (includePopularValue) {
                popularSearches = generatePopularSearches(searchTerm, cities, types);
                // Limiter le nombre de recherches populaires
                if (popularSearches.size() > limitValue) {
                    popularSearches = popularSearches.subList(0, limitValue);
                }
            }
        } else {
            // Si pas de recherche, retourner les recherches populaires par défaut (si activé)
            if (includePopularValue) {
                popularSearches = getDefaultPopularSearches();
                if (popularSearches.size() > limitValue) {
                    popularSearches = popularSearches.subList(0, limitValue);
                }
            }
        }

        // Limiter toutes les listes au maximum
        if (cities.size() > limitValue) {
            cities = cities.subList(0, limitValue);
        }
        if (types.size() > limitValue) {
            types = types.subList(0, limitValue);
        }
        if (addresses.size() > limitValue) {
            addresses = addresses.subList(0, limitValue);
        }
        if (titles.size() > limitValue) {
            titles = titles.subList(0, limitValue);
        }

        return new SearchSuggestionsDTO(cities, types, addresses, titles, popularSearches);
    }

    /**
     * Génère des recherches populaires basées sur les villes et types trouvés
     * Prend en compte les mots-clés de recherche comme "location", "vente", etc.
     */
    private List<String> generatePopularSearches(String searchTerm, List<String> cities, List<String> types) {
        List<String> suggestions = new java.util.ArrayList<>();
        
        // Mots-clés de recherche courants en immobilier
        List<String> keywords = extractSearchKeywords(searchTerm);
        
        // Normaliser les types (APARTMENT -> Appartement, etc.)
        List<String> normalizedTypes = normalizeTypes(types);
        if (normalizedTypes.isEmpty()) {
            normalizedTypes = normalizeTypes(propertyRepository.findDistinctTypesForPublishedProperties()
                    .stream().limit(5).collect(java.util.stream.Collectors.toList()));
        }
        
        // Normaliser les villes
        List<String> normalizedCities = cities;
        if (normalizedCities.isEmpty() && searchTerm.length() >= 2) {
            // Si aucune ville trouvée mais la recherche contient "paris", "lyon", etc.
            normalizedCities = propertyRepository.findDistinctCitiesForPublishedProperties()
                    .stream()
                    .filter(city -> searchTerm.contains(city.toLowerCase()) || city.toLowerCase().contains(searchTerm))
                    .limit(5)
                    .collect(java.util.stream.Collectors.toList());
        }
        if (normalizedCities.isEmpty()) {
            normalizedCities = propertyRepository.findDistinctCitiesForPublishedProperties()
                    .stream().limit(5).collect(java.util.stream.Collectors.toList());
        }

        // Générer des suggestions avec mots-clés + type + ville
        for (String keyword : keywords) {
            for (String type : normalizedTypes.stream().limit(3).collect(java.util.stream.Collectors.toList())) {
                for (String city : normalizedCities.stream().limit(3).collect(java.util.stream.Collectors.toList())) {
                    String suggestion = keyword + " " + type + " " + city;
                    if (!suggestions.contains(suggestion)) {
                        suggestions.add(suggestion);
                    }
                }
            }
        }

        // Si pas de mots-clés détectés, générer des suggestions simples type + ville
        if (keywords.isEmpty()) {
            for (String type : normalizedTypes.stream().limit(3).collect(java.util.stream.Collectors.toList())) {
                for (String city : normalizedCities.stream().limit(3).collect(java.util.stream.Collectors.toList())) {
                    String suggestion = type + " " + city;
                    if (!suggestions.contains(suggestion)) {
                        suggestions.add(suggestion);
                    }
                }
            }
        }

        // Ajouter aussi des suggestions avec "à" ou "a" si pertinent
        // Détecter si la requête contient "a" ou "à" suivi d'une ville
        boolean hasCityWithPreposition = searchTerm.contains(" a ") || searchTerm.contains(" à ") || 
                                         searchTerm.contains("paris") || searchTerm.contains("lyon") || 
                                         searchTerm.contains("marseille") || searchTerm.contains("bordeaux");
        
        if (hasCityWithPreposition || !normalizedCities.isEmpty()) {
            for (String keyword : keywords.isEmpty() ? List.of("location", "vente") : keywords) {
                for (String type : normalizedTypes.stream().limit(3).collect(java.util.stream.Collectors.toList())) {
                    for (String city : normalizedCities.stream().limit(3).collect(java.util.stream.Collectors.toList())) {
                        // Suggestion avec "à"
                        String suggestionWithA = keyword + " " + type + " à " + city;
                        if (!suggestions.contains(suggestionWithA)) {
                            suggestions.add(suggestionWithA);
                        }
                        // Suggestion avec "a" (sans accent)
                        String suggestionWithA2 = keyword + " " + type + " a " + city;
                        if (!suggestions.contains(suggestionWithA2)) {
                            suggestions.add(suggestionWithA2);
                        }
                    }
                }
            }
        }

        return suggestions.stream().limit(10).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Extrait les mots-clés de recherche de la requête
     */
    private List<String> extractSearchKeywords(String searchTerm) {
        List<String> keywords = new java.util.ArrayList<>();
        String[] commonKeywords = {"location", "vente", "achat", "louer", "vendre", "acheter", "recherche", "loué", "vendu"};
        
        // Extraire les mots de la requête
        String[] words = searchTerm.split("\\s+");
        
        for (String word : words) {
            for (String keyword : commonKeywords) {
                if (word.equalsIgnoreCase(keyword) || word.toLowerCase().contains(keyword)) {
                    if (!keywords.contains(keyword)) {
                        keywords.add(keyword);
                    }
                }
            }
        }
        
        // Si aucun mot-clé trouvé mais la recherche commence par un mot commun, l'ajouter
        if (keywords.isEmpty() && words.length > 0) {
            String firstWord = words[0].toLowerCase();
            for (String keyword : commonKeywords) {
                if (firstWord.startsWith(keyword) || keyword.startsWith(firstWord)) {
                    keywords.add(keyword);
                    break;
                }
            }
        }
        
        // Par défaut, ajouter "location" si aucun mot-clé trouvé (c'est le plus courant)
        if (keywords.isEmpty()) {
            keywords.add("location");
        }
        
        return keywords;
    }

    /**
     * Normalise les types de propriétés (APARTMENT -> Appartement, etc.)
     */
    private List<String> normalizeTypes(List<String> types) {
        return types.stream()
                .map(type -> {
                    String normalized = type.toLowerCase();
                    if (normalized.contains("apartment") || normalized.contains("appartement")) {
                        return "Appartement";
                    } else if (normalized.contains("house") || normalized.contains("maison")) {
                        return "Maison";
                    } else if (normalized.contains("villa")) {
                        return "Villa";
                    } else if (normalized.contains("studio")) {
                        return "Studio";
                    } else if (normalized.contains("land") || normalized.contains("terrain")) {
                        return "Terrain";
                    } else if (normalized.contains("commercial") || normalized.contains("bureau")) {
                        return "Bureau";
                    }
                    // Capitaliser la première lettre
                    return type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                })
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Retourne les recherches populaires par défaut
     */
    private List<String> getDefaultPopularSearches() {
        List<String> allCities = propertyRepository.findDistinctCitiesForPublishedProperties()
                .stream().limit(5).collect(java.util.stream.Collectors.toList());
        List<String> allTypes = propertyRepository.findDistinctTypesForPublishedProperties()
                .stream().limit(5).collect(java.util.stream.Collectors.toList());

        List<String> suggestions = new java.util.ArrayList<>();
        for (String type : allTypes) {
            for (String city : allCities) {
                suggestions.add(type + " " + city);
            }
        }

        return suggestions.stream().limit(10).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Vérifie si un mot est un mot vide (stop word)
     */
    private boolean isStopWord(String word) {
        String[] stopWords = {"a", "à", "de", "du", "le", "la", "les", "un", "une", "des", "et", "ou", "pour", "avec", "sans", "sur", "dans"};
        for (String stopWord : stopWords) {
            if (word.equalsIgnoreCase(stopWord)) {
                return true;
            }
        }
        return false;
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

    /**
     * Récupère une propriété publiée par son slug SEO-friendly
     * Utilisé pour les URLs optimisées pour le SEO
     */
    @Cacheable(value = "publicPropertyBySlug", key = "#slug")
    @Transactional(readOnly = true)
    public PropertyDTO getPublishedPropertyBySlug(String slug) {
        Property property = propertyRepository.findBySlug(slug)
                .orElseThrow(() -> new com.realestate.common.exception.ResourceNotFoundException("Property", slug));
        
        // Vérifier que la propriété est publiée
        if (!"PUBLISHED".equals(property.getStatus()) && !"AVAILABLE".equals(property.getStatus())) {
            throw new com.realestate.common.exception.ResourceNotFoundException("Property", slug);
        }
        
        return propertyMapper.toDTO(property);
    }
}


