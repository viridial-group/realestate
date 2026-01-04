package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Statistiques sur l'historique des prix d'une propriété")
public class PriceHistoryStatsDTO {

    @Schema(description = "Prix actuel")
    private BigDecimal currentPrice;

    @Schema(description = "Prix initial (le plus ancien)")
    private BigDecimal initialPrice;

    @Schema(description = "Prix minimum")
    private BigDecimal minPrice;

    @Schema(description = "Prix maximum")
    private BigDecimal maxPrice;

    @Schema(description = "Variation totale (prix actuel - prix initial)")
    private BigDecimal totalChange;

    @Schema(description = "Pourcentage de variation totale")
    private Double totalChangePercent;

    @Schema(description = "Nombre total de changements de prix")
    private Long totalChanges;

    @Schema(description = "Date du premier prix enregistré")
    private LocalDateTime firstPriceDate;

    @Schema(description = "Date du dernier changement de prix")
    private LocalDateTime lastPriceDate;

    @Schema(description = "Durée depuis le premier prix (en jours)")
    private Long daysSinceFirstPrice;

    @Schema(description = "Tendance (UP, DOWN, STABLE)")
    private String trend;

    // Constructors
    public PriceHistoryStatsDTO() {
    }

    // Getters and Setters
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(BigDecimal initialPrice) {
        this.initialPrice = initialPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getTotalChange() {
        return totalChange;
    }

    public void setTotalChange(BigDecimal totalChange) {
        this.totalChange = totalChange;
    }

    public Double getTotalChangePercent() {
        return totalChangePercent;
    }

    public void setTotalChangePercent(Double totalChangePercent) {
        this.totalChangePercent = totalChangePercent;
    }

    public Long getTotalChanges() {
        return totalChanges;
    }

    public void setTotalChanges(Long totalChanges) {
        this.totalChanges = totalChanges;
    }

    public LocalDateTime getFirstPriceDate() {
        return firstPriceDate;
    }

    public void setFirstPriceDate(LocalDateTime firstPriceDate) {
        this.firstPriceDate = firstPriceDate;
    }

    public LocalDateTime getLastPriceDate() {
        return lastPriceDate;
    }

    public void setLastPriceDate(LocalDateTime lastPriceDate) {
        this.lastPriceDate = lastPriceDate;
    }

    public Long getDaysSinceFirstPrice() {
        return daysSinceFirstPrice;
    }

    public void setDaysSinceFirstPrice(Long daysSinceFirstPrice) {
        this.daysSinceFirstPrice = daysSinceFirstPrice;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}

