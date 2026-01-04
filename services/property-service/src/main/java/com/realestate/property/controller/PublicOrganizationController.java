package com.realestate.property.controller;

import com.realestate.property.dto.OrganizationPerformanceStatsDTO;
import com.realestate.property.dto.OrganizationReviewCreateDTO;
import com.realestate.property.dto.OrganizationReviewDTO;
import com.realestate.property.dto.OrganizationReviewStatsDTO;
import com.realestate.property.dto.OrganizationWithReviewsDTO;
import com.realestate.property.service.OrganizationReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/organizations")
@Tag(name = "Public Organizations", description = "Public API for searching and viewing real estate agencies with reviews")
public class PublicOrganizationController {

    private final OrganizationReviewService organizationReviewService;

    public PublicOrganizationController(OrganizationReviewService organizationReviewService) {
        this.organizationReviewService = organizationReviewService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search organizations", description = "Search real estate agencies with reviews and statistics")
    public ResponseEntity<List<OrganizationWithReviewsDTO>> searchOrganizations(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) Double minRating) {
        
        List<OrganizationWithReviewsDTO> organizations = organizationReviewService.getOrganizationsWithReviews(
            city, postalCode, minRating);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{organizationId}")
    @Operation(summary = "Get organization with reviews", description = "Returns organization details with review statistics")
    public ResponseEntity<OrganizationWithReviewsDTO> getOrganizationWithReviews(@PathVariable Long organizationId) {
        OrganizationWithReviewsDTO organization = organizationReviewService.getOrganizationWithReviewsById(organizationId);
        if (organization == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(organization);
    }

    @GetMapping("/{organizationId}/reviews")
    @Operation(summary = "Get approved reviews for an organization", description = "Returns approved reviews for a real estate agency")
    public ResponseEntity<Page<OrganizationReviewDTO>> getApprovedReviews(
            @PathVariable Long organizationId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OrganizationReviewDTO> reviews = organizationReviewService.getApprovedReviews(organizationId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{organizationId}/reviews/stats")
    @Operation(summary = "Get review statistics", description = "Returns review statistics for an organization")
    public ResponseEntity<OrganizationReviewStatsDTO> getReviewStats(@PathVariable Long organizationId) {
        OrganizationReviewStatsDTO stats = organizationReviewService.getReviewStats(organizationId);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{organizationId}/reviews")
    @Operation(summary = "Create review for organization", description = "Creates a new review for a real estate agency")
    public ResponseEntity<OrganizationReviewDTO> createReview(
            @PathVariable Long organizationId,
            @Valid @RequestBody OrganizationReviewCreateDTO createDTO) {
        
        createDTO.setOrganizationId(organizationId);
        OrganizationReviewDTO created = organizationReviewService.createReview(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/reviews/{reviewId}/helpful")
    @Operation(summary = "Mark review as helpful", description = "Increments the helpful count for a review")
    public ResponseEntity<Void> markReviewAsHelpful(@PathVariable Long reviewId) {
        organizationReviewService.markAsHelpful(reviewId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{organizationId}/performance")
    @Operation(summary = "Get organization performance statistics", description = "Returns performance statistics including properties sold, average price, etc.")
    public ResponseEntity<OrganizationPerformanceStatsDTO> getPerformanceStats(@PathVariable Long organizationId) {
        OrganizationPerformanceStatsDTO stats = organizationReviewService.getPerformanceStats(organizationId);
        return ResponseEntity.ok(stats);
    }
}

