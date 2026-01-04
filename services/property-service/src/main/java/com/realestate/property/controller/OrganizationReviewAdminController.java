package com.realestate.property.controller;

import com.realestate.property.dto.OrganizationReviewDTO;
import com.realestate.property.service.OrganizationReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/organization-reviews")
@Tag(name = "Organization Reviews Admin", description = "Admin API for managing organization reviews")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class OrganizationReviewAdminController {

    private final OrganizationReviewService organizationReviewService;

    public OrganizationReviewAdminController(OrganizationReviewService organizationReviewService) {
        this.organizationReviewService = organizationReviewService;
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get all reviews for an organization", description = "Returns all reviews including pending ones")
    public ResponseEntity<Page<OrganizationReviewDTO>> getAllReviews(
            @PathVariable Long organizationId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OrganizationReviewDTO> reviews = organizationReviewService.getAllReviews(organizationId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{reviewId}/approve")
    @Operation(summary = "Approve a review", description = "Approves a pending review")
    public ResponseEntity<OrganizationReviewDTO> approveReview(@PathVariable Long reviewId) {
        OrganizationReviewDTO approved = organizationReviewService.approveReview(reviewId);
        return ResponseEntity.ok(approved);
    }

    @PostMapping("/{reviewId}/reject")
    @Operation(summary = "Reject a review", description = "Rejects a pending review")
    public ResponseEntity<OrganizationReviewDTO> rejectReview(@PathVariable Long reviewId) {
        OrganizationReviewDTO rejected = organizationReviewService.rejectReview(reviewId);
        return ResponseEntity.ok(rejected);
    }
}

