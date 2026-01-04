package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Statistiques globales DVF")
public class DVFStatsDTO {

    @Schema(description = "Nombre total de transactions")
    private Long totalTransactions;

    @Schema(description = "Nombre de départements couverts")
    private Long coveredDepartments;

    @Schema(description = "Années disponibles")
    private java.util.List<String> availableYears;

    @Schema(description = "Prix moyen au m² (toutes zones confondues)")
    private BigDecimal averagePricePerSquareMeter;

    @Schema(description = "Prix médian au m²")
    private BigDecimal medianPricePerSquareMeter;

    @Schema(description = "Dernière mise à jour")
    private java.time.LocalDateTime lastUpdate;

    @Schema(description = "Nombre d'imports terminés")
    private Long completedImports;

    @Schema(description = "Nombre d'imports en cours")
    private Long inProgressImports;

    // Getters and Setters
    public Long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Long getCoveredDepartments() {
        return coveredDepartments;
    }

    public void setCoveredDepartments(Long coveredDepartments) {
        this.coveredDepartments = coveredDepartments;
    }

    public java.util.List<String> getAvailableYears() {
        return availableYears;
    }

    public void setAvailableYears(java.util.List<String> availableYears) {
        this.availableYears = availableYears;
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

    public java.time.LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(java.time.LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getCompletedImports() {
        return completedImports;
    }

    public void setCompletedImports(Long completedImports) {
        this.completedImports = completedImports;
    }

    public Long getInProgressImports() {
        return inProgressImports;
    }

    public void setInProgressImports(Long inProgressImports) {
        this.inProgressImports = inProgressImports;
    }
}

