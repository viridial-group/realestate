package com.realestate.property.controller;

import com.realestate.common.document.PropertyDocument;
import com.realestate.property.service.PropertySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller pour la recherche Elasticsearch des Properties
 */
@RestController
@RequestMapping("/api/properties/search")
@Tag(name = "Property Search", description = "Elasticsearch search API for properties")
public class PropertySearchController {

    private final PropertySearchService propertySearchService;

    public PropertySearchController(PropertySearchService propertySearchService) {
        this.propertySearchService = propertySearchService;
    }

    @GetMapping("/text")
    @Operation(summary = "Full-text search", description = "Search properties by text in title and description")
    public ResponseEntity<Page<PropertyDocument>> searchByText(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyDocument> results = propertySearchService.searchByText(q, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Search by organization", description = "Search properties by organization ID")
    public ResponseEntity<Page<PropertyDocument>> searchByOrganization(
            @PathVariable Long organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyDocument> results = propertySearchService.searchByOrganization(organizationId, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Search by city", description = "Search properties by city")
    public ResponseEntity<List<PropertyDocument>> searchByCity(@PathVariable String city) {
        List<PropertyDocument> results = propertySearchService.searchByCity(city);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Search by type", description = "Search properties by type")
    public ResponseEntity<List<PropertyDocument>> searchByType(@PathVariable String type) {
        List<PropertyDocument> results = propertySearchService.searchByType(type);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Search by status", description = "Search properties by status")
    public ResponseEntity<List<PropertyDocument>> searchByStatus(@PathVariable String status) {
        List<PropertyDocument> results = propertySearchService.searchByStatus(status);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/price")
    @Operation(summary = "Search by price range", description = "Search properties by price range")
    public ResponseEntity<List<PropertyDocument>> searchByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<PropertyDocument> results = propertySearchService.searchByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/organization/{organizationId}/city/{city}")
    @Operation(summary = "Search by organization and city", description = "Search properties by organization and city")
    public ResponseEntity<Page<PropertyDocument>> searchByOrganizationAndCity(
            @PathVariable Long organizationId,
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyDocument> results = propertySearchService.searchByOrganizationAndCity(
                organizationId, city, pageable);
        return ResponseEntity.ok(results);
    }
}

