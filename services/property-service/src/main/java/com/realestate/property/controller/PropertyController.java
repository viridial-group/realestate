package com.realestate.property.controller;

import com.realestate.property.dto.PropertyDTO;
import com.realestate.property.entity.Property;
import com.realestate.property.mapper.PropertyMapper;
import com.realestate.property.service.PropertyService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Properties", description = "Real estate property management API")
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;

    public PropertyController(PropertyService propertyService, PropertyMapper propertyMapper) {
        this.propertyService = propertyService;
        this.propertyMapper = propertyMapper;
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
    @Operation(summary = "List properties", description = "Returns a list of properties filtered by various criteria")
    public ResponseEntity<List<PropertyDTO>> getProperties(
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
}
