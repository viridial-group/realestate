package com.realestate.property.controller;

import com.realestate.property.dto.ReviewCreateDTO;
import com.realestate.property.dto.ReviewDTO;
import com.realestate.property.dto.ReviewStatsDTO;
import com.realestate.property.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Review management API")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Create review", description = "Creates a new review for a property")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewCreateDTO reviewCreateDTO) {
        ReviewDTO created = reviewService.createReview(reviewCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get approved reviews by property", description = "Returns approved reviews for a property")
    public ResponseEntity<Page<ReviewDTO>> getApprovedReviewsByProperty(
            @PathVariable Long propertyId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getApprovedReviewsByPropertyId(propertyId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/property/{propertyId}/all")
    @Operation(summary = "Get all reviews by property (admin)", description = "Returns all reviews for a property including pending ones")
    public ResponseEntity<Page<ReviewDTO>> getAllReviewsByProperty(
            @PathVariable Long propertyId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getReviewsByPropertyId(propertyId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/property/{propertyId}/stats")
    @Operation(summary = "Get review statistics", description = "Returns review statistics for a property")
    public ResponseEntity<ReviewStatsDTO> getReviewStats(@PathVariable Long propertyId) {
        ReviewStatsDTO stats = reviewService.getReviewStatsByPropertyId(propertyId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID", description = "Returns a review by its ID")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        ReviewDTO review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by user", description = "Returns all reviews by a specific user")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getReviewsByUserId(userId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get reviews by status (admin)", description = "Returns reviews filtered by status")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByStatus(
            @PathVariable String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getReviewsByStatus(status, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update review status (admin)", description = "Updates the status of a review (PENDING, APPROVED, REJECTED)")
    public ResponseEntity<ReviewDTO> updateReviewStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ReviewDTO updated = reviewService.updateReviewStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/helpful")
    @Operation(summary = "Mark review as helpful", description = "Increments the helpful count for a review")
    public ResponseEntity<ReviewDTO> markHelpful(@PathVariable Long id) {
        ReviewDTO updated = reviewService.markHelpful(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review", description = "Soft deletes a review")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}

