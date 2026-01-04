package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Statistiques des avis pour une propriété")
public class ReviewStatsDTO {

    @Schema(description = "Note moyenne")
    private Double averageRating;

    @Schema(description = "Nombre total d'avis approuvés")
    private Long totalReviews;

    @Schema(description = "Distribution des notes (rating -> count)")
    private Map<Integer, Long> ratingDistribution;

    @Schema(description = "Pourcentage de chaque note")
    private Map<Integer, Double> ratingPercentages;

    // Constructors
    public ReviewStatsDTO() {
    }

    public ReviewStatsDTO(Double averageRating, Long totalReviews) {
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    // Getters and Setters
    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Map<Integer, Long> getRatingDistribution() {
        return ratingDistribution;
    }

    public void setRatingDistribution(Map<Integer, Long> ratingDistribution) {
        this.ratingDistribution = ratingDistribution;
    }

    public Map<Integer, Double> getRatingPercentages() {
        return ratingPercentages;
    }

    public void setRatingPercentages(Map<Integer, Double> ratingPercentages) {
        this.ratingPercentages = ratingPercentages;
    }
}

