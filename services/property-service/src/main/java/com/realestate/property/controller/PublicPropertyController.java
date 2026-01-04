package com.realestate.property.controller;

import com.realestate.property.dto.PagedPropertyResponse;
import com.realestate.property.dto.PropertyDTO;
import com.realestate.property.dto.SearchSuggestionsDTO;
import com.realestate.property.service.PublicPropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller public pour l'affichage des propriétés sur le frontend public
 * Ne nécessite pas d'authentification
 * Filtre automatiquement les propriétés publiées/disponibles
 */
@RestController
@RequestMapping("/api/public/properties")
@Tag(name = "Public Properties", description = "Public API for browsing published properties with performance optimizations")
public class PublicPropertyController {

    private final PublicPropertyService publicPropertyService;
    private final com.realestate.property.service.PropertyEventService propertyEventService;

    public PublicPropertyController(
            PublicPropertyService publicPropertyService,
            com.realestate.property.service.PropertyEventService propertyEventService) {
        this.publicPropertyService = publicPropertyService;
        this.propertyEventService = propertyEventService;
    }

    @GetMapping
    @Operation(
        summary = "List published properties (optimized)",
        description = "Returns a paginated list of published/available properties for public display. " +
                     "Performance optimizations: " +
                     "- Uses Elasticsearch for complex text searches (if available) " +
                     "- Falls back to PostgreSQL with JPA Specifications for simple filters " +
                     "- Redis caching for frequent queries " +
                     "- Automatic filtering by status PUBLISHED or AVAILABLE"
    )
    public ResponseEntity<PagedPropertyResponse> getPublishedProperties(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minSurface,
            @RequestParam(required = false) BigDecimal maxSurface,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Integer bathrooms,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        // Limiter la taille de page pour éviter les surcharges
        if (size > 100) {
            size = 100;
        }
        
            PagedPropertyResponse response = publicPropertyService.getPublishedProperties(
                    organizationId, type, city, country, minPrice, maxPrice, minSurface, maxSurface,
                    bedrooms, bathrooms, search, sortBy, transactionType, createdAfter, page, size);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cities")
    @Operation(
        summary = "Get list of available cities",
        description = "Returns a list of cities that have published properties. " +
                     "Useful for autocomplete and filtering."
    )
    public ResponseEntity<List<String>> getAvailableCities(
            @RequestParam(required = false) String search) {
        List<String> cities = publicPropertyService.getAvailableCities(search);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/suggestions")
    @Operation(
        summary = "Get search suggestions (enhanced)",
        description = "Returns comprehensive search suggestions including cities, types, addresses, titles, and popular searches. " +
                     "Enhanced with limit, includePopular, and includeTrending parameters. " +
                     "Useful for autocomplete during typing."
    )
    public ResponseEntity<SearchSuggestionsDTO> getSearchSuggestions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Boolean includePopular,
            @RequestParam(required = false) Boolean includeTrending) {
        SearchSuggestionsDTO suggestions = publicPropertyService.getSearchSuggestions(
            search, limit, includePopular, includeTrending);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get published property by ID (cached)",
        description = "Returns property information for a specific property ID. " +
                     "Only returns if property is published/available. " +
                     "Results are cached in Redis for performance. " +
                     "Automatically tracks a VIEW event."
    )
    public ResponseEntity<PropertyDTO> getPublishedPropertyById(@PathVariable Long id) {
        PropertyDTO property = publicPropertyService.getPublishedPropertyById(id);
        
        // Tracker une vue (de manière asynchrone pour ne pas ralentir la réponse)
        try {
            propertyEventService.trackView(id, null, "{\"source\":\"public_api\"}");
        } catch (Exception e) {
            // Ignorer les erreurs de tracking pour ne pas bloquer la réponse
        }
        
        return ResponseEntity.ok(property);
    }

    @GetMapping("/reference/{reference}")
    @Operation(
        summary = "Get published property by reference (cached)",
        description = "Returns property information for a specific property reference. " +
                     "Only returns if property is published/available. " +
                     "Results are cached in Redis for performance."
    )
    public ResponseEntity<PropertyDTO> getPublishedPropertyByReference(@PathVariable String reference) {
        PropertyDTO property = publicPropertyService.getPublishedPropertyByReference(reference);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/slug/{slug}")
    @Operation(
        summary = "Get published property by slug (SEO-friendly, cached)",
        description = "Returns property information for a specific property slug (SEO-friendly URL). " +
                     "Only returns if property is published/available. " +
                     "Results are cached in Redis for performance. " +
                     "Example: /api/public/properties/slug/appartement-paris-3-pieces-123"
    )
    public ResponseEntity<PropertyDTO> getPublishedPropertyBySlug(@PathVariable String slug) {
        PropertyDTO property = publicPropertyService.getPublishedPropertyBySlug(slug);
        return ResponseEntity.ok(property);
    }
}
