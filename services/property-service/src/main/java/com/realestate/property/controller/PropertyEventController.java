package com.realestate.property.controller;

import com.realestate.property.entity.PropertyEvent;
import com.realestate.property.service.PropertyEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller pour le tracking des événements de propriétés
 */
@RestController
@RequestMapping("/api/properties")
@Tag(name = "Property Events", description = "Property event tracking API")
public class PropertyEventController {

    private final PropertyEventService propertyEventService;

    public PropertyEventController(PropertyEventService propertyEventService) {
        this.propertyEventService = propertyEventService;
    }

    @PostMapping("/{propertyId}/events/view")
    @Operation(
        summary = "Track property view",
        description = "Records a view event for a property. Can be called from frontend when a property is viewed."
    )
    public ResponseEntity<PropertyEvent> trackView(
            @PathVariable Long propertyId,
            @RequestParam(required = false) Long userId,
            @RequestBody(required = false) Map<String, Object> metadata) {
        String metadataJson = metadata != null ? convertToJson(metadata) : null;
        PropertyEvent event = propertyEventService.trackView(propertyId, userId, metadataJson);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PostMapping("/{propertyId}/events/contact")
    @Operation(
        summary = "Track property contact",
        description = "Records a contact event for a property. Called when a user contacts about a property."
    )
    public ResponseEntity<PropertyEvent> trackContact(
            @PathVariable Long propertyId,
            @RequestParam(required = false) Long userId,
            @RequestBody(required = false) Map<String, Object> metadata) {
        String metadataJson = metadata != null ? convertToJson(metadata) : null;
        PropertyEvent event = propertyEventService.trackContact(propertyId, userId, metadataJson);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PostMapping("/{propertyId}/events/favorite")
    @Operation(
        summary = "Track property favorite",
        description = "Records a favorite event for a property. Called when a user adds a property to favorites."
    )
    public ResponseEntity<PropertyEvent> trackFavorite(
            @PathVariable Long propertyId,
            @RequestParam(required = false) Long userId,
            @RequestBody(required = false) Map<String, Object> metadata) {
        String metadataJson = metadata != null ? convertToJson(metadata) : null;
        PropertyEvent event = propertyEventService.trackFavorite(propertyId, userId, metadataJson);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PostMapping("/{propertyId}/events/share")
    @Operation(
        summary = "Track property share",
        description = "Records a share event for a property. Called when a user shares a property."
    )
    public ResponseEntity<PropertyEvent> trackShare(
            @PathVariable Long propertyId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String platform,
            @RequestBody(required = false) Map<String, Object> metadata) {
        String metadataJson = metadata != null ? convertToJson(metadata) : null;
        PropertyEvent event = propertyEventService.trackShare(propertyId, userId, platform, metadataJson);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    /**
     * Convertit une Map en JSON string (simple)
     */
    private String convertToJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        // Utiliser Jackson ou une simple conversion
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
}

