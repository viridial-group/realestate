package com.realestate.property.controller;

import com.realestate.property.dto.PropertyDTO;
import com.realestate.property.entity.Property;
import com.realestate.property.mapper.PropertyMapper;
import com.realestate.property.service.PropertyService;
import com.realestate.property.service.ContactMessageService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Properties", description = "Real estate property management API")
public class PropertyController {

    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;
    private final ContactMessageService contactMessageService;
    private final com.realestate.property.service.StatsService statsService;
    private final com.realestate.property.service.PropertyEventService propertyEventService;
    private final String identityServiceUrl;

    public PropertyController(
            PropertyService propertyService, 
            PropertyMapper propertyMapper,
            ContactMessageService contactMessageService,
            com.realestate.property.service.StatsService statsService,
            com.realestate.property.service.PropertyEventService propertyEventService,
            @Value("${services.identity.url:http://localhost:8081}") String identityServiceUrl) {
        this.propertyService = propertyService;
        this.propertyMapper = propertyMapper;
        this.contactMessageService = contactMessageService;
        this.statsService = statsService;
        this.propertyEventService = propertyEventService;
        this.identityServiceUrl = identityServiceUrl;
    }

    @PostMapping
    @Operation(summary = "Create property", description = "Creates a new real estate property")
    public ResponseEntity<PropertyDTO> createProperty(
            @Valid @RequestBody PropertyDTO propertyDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Property property = propertyMapper.toEntity(propertyDTO);
        String token = authorization != null && authorization.startsWith("Bearer ") 
                ? authorization.substring(7) 
                : null;
        Property created = propertyService.createProperty(property, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyMapper.toDTO(created));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my properties", description = "Returns properties owned by the authenticated user")
    public ResponseEntity<Map<String, Object>> getMyProperties(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        try {
            // Extraire le token
            String token = authorization != null && authorization.startsWith("Bearer ") 
                    ? authorization.substring(7) 
                    : null;
            
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Appeler /api/identity/users/me pour obtenir l'utilisateur actuel via WebClient
            WebClient webClient = WebClient.builder()
                    .baseUrl(identityServiceUrl)
                    .build();
            
            com.realestate.common.client.dto.UserInfoDTO currentUser = webClient
                    .get()
                    .uri("/api/identity/users/me")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(com.realestate.common.client.dto.UserInfoDTO.class)
                    .block();
            
            if (currentUser == null || currentUser.getId() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Long userId = currentUser.getId();
            
            // Récupérer les propriétés avec pagination en filtrant par createdBy
            Pageable pageable = PageRequest.of(page, size);
            Page<Property> propertyPage = propertyService.getPropertiesByCreatedBy(userId, status, pageable);
            
            // Convertir en DTO
            Page<PropertyDTO> dtoPage = propertyPage.map(propertyMapper::toDTO);
            
            // Construire la réponse au format attendu par le frontend
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("content", dtoPage.getContent());
            response.put("currentPage", dtoPage.getNumber());
            response.put("totalPages", dtoPage.getTotalPages());
            response.put("totalElements", dtoPage.getTotalElements());
            response.put("size", dtoPage.getSize());
            response.put("first", dtoPage.isFirst());
            response.put("last", dtoPage.isLast());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching my properties", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property by ID", description = "Returns property information for a specific property ID")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        Property property = propertyService.getPropertyById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", id));
        return ResponseEntity.ok(propertyMapper.toDTO(property));
    }

    @GetMapping("/reference/{reference}")
    @Operation(summary = "Get property by reference", description = "Returns property information for a specific property reference")
    public ResponseEntity<PropertyDTO> getPropertyByReference(@PathVariable String reference) {
        Property property = propertyService.getPropertyByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Property", reference));
        return ResponseEntity.ok(propertyMapper.toDTO(property));
    }

    @GetMapping
    @Operation(summary = "List properties", description = "Returns a list of properties filtered by various criteria using JPA Specifications")
    public ResponseEntity<List<PropertyDTO>> getProperties(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String status,
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
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "1000") int size) {
        
        // Si aucun filtre n'est spécifié et qu'on veut toutes les propriétés, utiliser getAllPropertiesWithFilters
        // Sinon, utiliser la pagination si size < 1000
        List<Property> properties;
        
        if (size >= 1000 || (organizationId == null && assignedUserId == null && teamId == null 
                && status == null && type == null && city == null && country == null 
                && minPrice == null && maxPrice == null && minSurface == null && maxSurface == null
                && bedrooms == null && bathrooms == null && search == null)) {
            // Récupérer toutes les propriétés sans pagination
            properties = propertyService.getAllPropertiesWithFilters(
                    organizationId, assignedUserId, teamId, status, type, city, country,
                    minPrice, maxPrice, minSurface, maxSurface, bedrooms, bathrooms, search);
        } else {
            // Utiliser la pagination
            Pageable pageable = PageRequest.of(page, size);
            Page<Property> propertyPage = propertyService.getPropertiesWithFilters(
                    organizationId, assignedUserId, teamId, status, type, city, country,
                    minPrice, maxPrice, minSurface, maxSurface, bedrooms, bathrooms, search, pageable);
            properties = propertyPage.getContent();
        }

        List<PropertyDTO> propertyDTOs = properties.stream()
                .map(propertyMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(propertyDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property", description = "Updates property information for a specific property ID")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyDTO propertyDTO) {
        Property property = propertyMapper.toEntity(propertyDTO);
        Property updated = propertyService.updateProperty(id, property);
        return ResponseEntity.ok(propertyMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete property", description = "Deletes a property from the database by ID")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/share")
    @Operation(summary = "Share property with organization", description = "Shares a property with another organization or user with specific permissions")
    public ResponseEntity<com.realestate.property.entity.PropertyAccess> shareProperty(
            @PathVariable Long id,
            @RequestBody Map<String, Object> shareRequest) {
        Long organizationId = Long.valueOf(shareRequest.get("organizationId").toString());
        Long userId = shareRequest.containsKey("userId") ? Long.valueOf(shareRequest.get("userId").toString()) : null;
        Boolean canRead = shareRequest.containsKey("canRead") ? (Boolean) shareRequest.get("canRead") : true;
        Boolean canWrite = shareRequest.containsKey("canWrite") ? (Boolean) shareRequest.get("canWrite") : false;
        Boolean canDelete = shareRequest.containsKey("canDelete") ? (Boolean) shareRequest.get("canDelete") : false;
        Long grantedBy = shareRequest.containsKey("grantedBy") ? Long.valueOf(shareRequest.get("grantedBy").toString()) : null;

        com.realestate.property.entity.PropertyAccess shared = propertyService.sharePropertyWithOrganization(
                id, organizationId, userId, canRead, canWrite, canDelete, grantedBy);
        return ResponseEntity.ok(shared);
    }

    @PostMapping("/{id}/features")
    @Operation(summary = "Add feature to property", description = "Adds or updates a feature for a property")
    public ResponseEntity<com.realestate.property.entity.PropertyFeature> addFeatureToProperty(
            @PathVariable Long id,
            @Valid @RequestBody com.realestate.property.entity.PropertyFeature feature) {
        com.realestate.property.entity.PropertyFeature added = propertyService.addFeatureToProperty(id, feature);
        return ResponseEntity.ok(added);
    }

    @DeleteMapping("/{id}/features/{key}")
    @Operation(summary = "Remove feature from property", description = "Removes a feature from a property by key")
    public ResponseEntity<Void> removeFeatureFromProperty(
            @PathVariable Long id,
            @PathVariable String key) {
        propertyService.removeFeatureFromProperty(id, key);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/features")
    @Operation(summary = "Get all features for property", description = "Returns all active features for a property")
    public ResponseEntity<List<com.realestate.property.entity.PropertyFeature>> getPropertyFeatures(
            @PathVariable Long id) {
        List<com.realestate.property.entity.PropertyFeature> features = propertyService.getPropertyFeatures(id);
        return ResponseEntity.ok(features);
    }

    @PostMapping("/{id}/features/batch")
    @Operation(summary = "Add or update multiple features", description = "Adds or updates multiple features for a property")
    public ResponseEntity<List<com.realestate.property.entity.PropertyFeature>> addFeaturesToProperty(
            @PathVariable Long id,
            @Valid @RequestBody List<com.realestate.property.entity.PropertyFeature> features) {
        List<com.realestate.property.entity.PropertyFeature> added = propertyService.addFeaturesToProperty(id, features);
        return ResponseEntity.ok(added);
    }

    @PostMapping("/{id}/features/sync/{key}")
    @Operation(summary = "Sync features from JSON array", description = "Synchronizes features from a JSON array, replacing existing ones with the same key")
    public ResponseEntity<List<com.realestate.property.entity.PropertyFeature>> syncFeaturesFromJsonArray(
            @PathVariable Long id,
            @PathVariable String key,
            @RequestBody List<String> values) {
        List<com.realestate.property.entity.PropertyFeature> synced = propertyService.syncFeaturesFromJsonArray(id, key, values);
        return ResponseEntity.ok(synced);
    }

    @PostMapping("/unread-messages-count")
    @Operation(
        summary = "Get unread messages count for properties", 
        description = "Returns a map of property IDs to unread messages count. This endpoint is designed to be called asynchronously after loading the property list, with a loading indicator."
    )
    public ResponseEntity<Map<Long, Long>> getUnreadMessagesCount(
            @RequestBody List<Long> propertyIds) {
        if (propertyIds == null || propertyIds.isEmpty()) {
            return ResponseEntity.ok(Map.of());
        }
        Map<Long, Long> counts = contactMessageService.countUnreadMessagesByPropertyIds(propertyIds);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/{id}/stats")
    @Operation(
        summary = "Get property statistics",
        description = "Returns current statistics for a property (views, contacts, favorites, shares)"
    )
    public ResponseEntity<Map<String, Long>> getPropertyStats(@PathVariable Long id) {
        try {
            Map<String, Long> stats = propertyEventService.getPropertyCurrentStats(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Si le service n'est pas disponible, retourner des stats par défaut
            Map<String, Long> stats = Map.of(
                "views", 0L,
                "contacts", 0L,
                "favorites", 0L,
                "shares", 0L
            );
            return ResponseEntity.ok(stats);
        }
    }

    @GetMapping("/{id}/stats/history")
    @Operation(
        summary = "Get property statistics history",
        description = "Returns statistics history for a specific property over a specified number of days. " +
                      "Cached for 5 minutes. Default: 7 days, max: 90 days."
    )
    public ResponseEntity<List<com.realestate.property.dto.StatsHistoryPointDTO>> getPropertyStatsHistory(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        List<com.realestate.property.dto.StatsHistoryPointDTO> history = 
            statsService.getPropertyStatsHistory(id, days);
        return ResponseEntity.ok(history);
    }
}
