package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour les données de marché immobilier basées sur DVF
 */
@Schema(description = "Données de marché immobilier pour une zone")
public class MarketDataDTO {

    @Schema(description = "Code postal ou commune")
    private String location;

    @Schema(description = "Type de bien (Maison, Appartement, etc.)")
    private String propertyType;

    @Schema(description = "Prix moyen au m²")
    private BigDecimal averagePricePerSquareMeter;

    @Schema(description = "Prix médian au m²")
    private BigDecimal medianPricePerSquareMeter;

    @Schema(description = "Prix minimum au m²")
    private BigDecimal minPricePerSquareMeter;

    @Schema(description = "Prix maximum au m²")
    private BigDecimal maxPricePerSquareMeter;

    @Schema(description = "Nombre de transactions")
    private Long transactionCount;

    @Schema(description = "Période analysée - Date de début")
    private LocalDate periodStart;

    @Schema(description = "Période analysée - Date de fin")
    private LocalDate periodEnd;

    @Schema(description = "Évolution du prix au m² par trimestre")
    private List<PriceEvolutionDTO> priceEvolution;

    @Schema(description = "Comparaison avec la propriété (si fournie)")
    private PropertyComparisonDTO comparison;

    // Getters and Setters
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public BigDecimal getAveragePricePerSquareMeter() {
        return averagePricePerSquareMeter;
    }

    public void setAveragePricePerSquareMeter(BigDecimal averagePricePerSquareMeter) {
        this.averagePricePerSquareMeter = averagePricePerSquareMeter;
    }

    public BigDecimal getMedianPricePerSquareMeter() {
        return medianPricePerSquareMeter;
    }

    public void setMedianPricePerSquareMeter(BigDecimal medianPricePerSquareMeter) {
        this.medianPricePerSquareMeter = medianPricePerSquareMeter;
    }

    public BigDecimal getMinPricePerSquareMeter() {
        return minPricePerSquareMeter;
    }

    public void setMinPricePerSquareMeter(BigDecimal minPricePerSquareMeter) {
        this.minPricePerSquareMeter = minPricePerSquareMeter;
    }

    public BigDecimal getMaxPricePerSquareMeter() {
        return maxPricePerSquareMeter;
    }

    public void setMaxPricePerSquareMeter(BigDecimal maxPricePerSquareMeter) {
        this.maxPricePerSquareMeter = maxPricePerSquareMeter;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public List<PriceEvolutionDTO> getPriceEvolution() {
        return priceEvolution;
    }

    public void setPriceEvolution(List<PriceEvolutionDTO> priceEvolution) {
        this.priceEvolution = priceEvolution;
    }

    public PropertyComparisonDTO getComparison() {
        return comparison;
    }

    public void setComparison(PropertyComparisonDTO comparison) {
        this.comparison = comparison;
    }

    @Schema(description = "Évolution du prix par période")
    public static class PriceEvolutionDTO {
        @Schema(description = "Période (trimestre)")
        private String period;

        @Schema(description = "Prix moyen au m²")
        private BigDecimal averagePrice;

        @Schema(description = "Nombre de transactions")
        private Long count;

        // Getters and Setters
        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public BigDecimal getAveragePrice() {
            return averagePrice;
        }

        public void setAveragePrice(BigDecimal averagePrice) {
            this.averagePrice = averagePrice;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    @Schema(description = "Comparaison avec une propriété")
    public static class PropertyComparisonDTO {
        @Schema(description = "Prix de la propriété au m²")
        private BigDecimal propertyPricePerSquareMeter;

        @Schema(description = "Différence en pourcentage par rapport au marché")
        private BigDecimal priceDifferencePercent;

        @Schema(description = "Évaluation du prix (SURESTIMÉ, SOUS-ESTIMÉ, CORRECT)")
        private String priceEvaluation;

        @Schema(description = "Recommandation")
        private String recommendation;

        // Getters and Setters
        public BigDecimal getPropertyPricePerSquareMeter() {
            return propertyPricePerSquareMeter;
        }

        public void setPropertyPricePerSquareMeter(BigDecimal propertyPricePerSquareMeter) {
            this.propertyPricePerSquareMeter = propertyPricePerSquareMeter;
        }

        public BigDecimal getPriceDifferencePercent() {
            return priceDifferencePercent;
        }

        public void setPriceDifferencePercent(BigDecimal priceDifferencePercent) {
            this.priceDifferencePercent = priceDifferencePercent;
        }

        public String getPriceEvaluation() {
            return priceEvaluation;
        }

        public void setPriceEvaluation(String priceEvaluation) {
            this.priceEvaluation = priceEvaluation;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
    }
}

