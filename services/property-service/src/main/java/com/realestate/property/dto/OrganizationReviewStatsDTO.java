package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statistiques des avis pour une organisation")
public class OrganizationReviewStatsDTO {

    @Schema(description = "Note moyenne")
    private Double averageRating;

    @Schema(description = "Nombre total d'avis")
    private Long totalReviews;

    @Schema(description = "Nombre d'avis par note (1 à 5)")
    private RatingDistributionDTO ratingDistribution;

    @Schema(description = "Pourcentage de clients vérifiés")
    private Double verifiedClientPercentage;

    // Constructors
    public OrganizationReviewStatsDTO() {
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

    public RatingDistributionDTO getRatingDistribution() {
        return ratingDistribution;
    }

    public void setRatingDistribution(RatingDistributionDTO ratingDistribution) {
        this.ratingDistribution = ratingDistribution;
    }

    public Double getVerifiedClientPercentage() {
        return verifiedClientPercentage;
    }

    public void setVerifiedClientPercentage(Double verifiedClientPercentage) {
        this.verifiedClientPercentage = verifiedClientPercentage;
    }

    @Schema(description = "Distribution des notes")
    public static class RatingDistributionDTO {
        @Schema(description = "Nombre d'avis 5 étoiles")
        private Long fiveStars = 0L;

        @Schema(description = "Nombre d'avis 4 étoiles")
        private Long fourStars = 0L;

        @Schema(description = "Nombre d'avis 3 étoiles")
        private Long threeStars = 0L;

        @Schema(description = "Nombre d'avis 2 étoiles")
        private Long twoStars = 0L;

        @Schema(description = "Nombre d'avis 1 étoile")
        private Long oneStar = 0L;

        // Getters and Setters
        public Long getFiveStars() {
            return fiveStars;
        }

        public void setFiveStars(Long fiveStars) {
            this.fiveStars = fiveStars;
        }

        public Long getFourStars() {
            return fourStars;
        }

        public void setFourStars(Long fourStars) {
            this.fourStars = fourStars;
        }

        public Long getThreeStars() {
            return threeStars;
        }

        public void setThreeStars(Long threeStars) {
            this.threeStars = threeStars;
        }

        public Long getTwoStars() {
            return twoStars;
        }

        public void setTwoStars(Long twoStars) {
            this.twoStars = twoStars;
        }

        public Long getOneStar() {
            return oneStar;
        }

        public void setOneStar(Long oneStar) {
            this.oneStar = oneStar;
        }
    }
}

