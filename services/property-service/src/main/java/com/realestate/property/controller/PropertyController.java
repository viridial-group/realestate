package com.realestate.property.controller;

import com.realestate.property.entity.Property;
import com.realestate.property.entity.PropertyFeature;
import com.realestate.property.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Properties", description = "Real estate property management API")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    @Operation(summary = "Create property", description = "Creates a new real estate property")
    public ResponseEntity<Property> createProperty(@Valid @RequestBody Property property) {
        Property created = propertyService.createProperty(property);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property by ID", description = "Returns property information for a specific property ID")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    @Operation(summary = "Get property by reference", description = "Returns property information for a specific property reference")
    public ResponseEntity<Property> getPropertyByReference(@PathVariable String reference) {
        return propertyService.getPropertyByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List properties", description = "Returns a list of properties filtered by various criteria")
    public ResponseEntity<List<Property>> getProperties(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minSurface,
            @RequestParam(required = false) BigDecimal maxSurface) {
        List<Property> properties;

        if (organizationId != null && status != null) {
            properties = propertyService.getPropertiesByOrganizationId(organizationId)
                    .stream()
                    .filter(p -> p.getStatus().equals(status))
                    .toList();
        } else if (organizationId != null) {
            properties = propertyService.getPropertiesByOrganizationId(organizationId);
        } else if (assignedUserId != null) {
            properties = propertyService.getPropertiesByAssignedUserId(assignedUserId);
        } else if (teamId != null) {
            properties = propertyService.getPropertiesByTeamId(teamId);
        } else if (status != null) {
            properties = propertyService.getPropertiesByStatus(status);
        } else if (type != null) {
            properties = propertyService.getPropertiesByType(type);
        } else if (city != null) {
            properties = propertyService.getPropertiesByCity(city);
        } else if (minPrice != null && maxPrice != null) {
            properties = propertyService.getPropertiesByPriceRange(minPrice, maxPrice);
        } else if (minSurface != null && maxSurface != null) {
            properties = propertyService.getPropertiesBySurfaceRange(minSurface, maxSurface);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property", description = "Updates property information for a specific property ID")
    public ResponseEntity<Property> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody Property propertyDetails) {
        try {
            Property updated = propertyService.updateProperty(id, propertyDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete property", description = "Deletes a property from the database by ID")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/share")
    @Operation(summary = "Share property with organization", description = "Shares a property with another organization or user with specific permissions")
    public ResponseEntity<com.realestate.property.entity.PropertyAccess> shareProperty(
            @PathVariable Long id,
            @RequestBody Map<String, Object> shareRequest) {
        try {
            Long organizationId = Long.valueOf(shareRequest.get("organizationId").toString());
            Long userId = shareRequest.containsKey("userId") ? Long.valueOf(shareRequest.get("userId").toString()) : null;
            Boolean canRead = shareRequest.containsKey("canRead") ? (Boolean) shareRequest.get("canRead") : true;
            Boolean canWrite = shareRequest.containsKey("canWrite") ? (Boolean) shareRequest.get("canWrite") : false;
            Boolean canDelete = shareRequest.containsKey("canDelete") ? (Boolean) shareRequest.get("canDelete") : false;
            Long grantedBy = shareRequest.containsKey("grantedBy") ? Long.valueOf(shareRequest.get("grantedBy").toString()) : null;

            com.realestate.property.entity.PropertyAccess shared = propertyService.sharePropertyWithOrganization(
                    id, organizationId, userId, canRead, canWrite, canDelete, grantedBy);
            return ResponseEntity.ok(shared);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/features")
    @Operation(summary = "Add feature to property", description = "Adds or updates a feature for a property")
    public ResponseEntity<PropertyFeature> addFeatureToProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyFeature feature) {
        try {
            PropertyFeature added = propertyService.addFeatureToProperty(id, feature);
            return ResponseEntity.ok(added);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/features/{key}")
    @Operation(summary = "Remove feature from property", description = "Removes a feature from a property by key")
    public ResponseEntity<Void> removeFeatureFromProperty(
            @PathVariable Long id,
            @PathVariable String key) {
        try {
            propertyService.removeFeatureFromProperty(id, key);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

