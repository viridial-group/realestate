package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Schema(description = "Statistiques de performance d'une organisation")
public class OrganizationPerformanceStatsDTO {

    @Schema(description = "Nombre total de propriétés")
    private Long totalProperties;

    @Schema(description = "Propriétés disponibles")
    private Long availableProperties;

    @Schema(description = "Propriétés vendues")
    private Long soldProperties;

    @Schema(description = "Propriétés louées")
    private Long rentedProperties;

    @Schema(description = "Prix moyen des propriétés")
    private BigDecimal averagePrice;

    @Schema(description = "Prix minimum")
    private BigDecimal minPrice;

    @Schema(description = "Prix maximum")
    private BigDecimal maxPrice;

    @Schema(description = "Surface moyenne")
    private BigDecimal averageSurface;

    @Schema(description = "Propriétés ajoutées ce mois")
    private Long newThisMonth;

    @Schema(description = "Propriétés ajoutées cette semaine")
    private Long newThisWeek;

    @Schema(description = "Répartition par type")
    private Map<String, Long> byType;

    @Schema(description = "Répartition par ville")
    private Map<String, Long> byCity;

    @Schema(description = "Répartition par statut")
    private Map<String, Long> byStatus;

    @Schema(description = "Villes desservies")
    private List<String> servedCities;

    @Schema(description = "Types de propriétés gérés")
    private List<String> propertyTypes;

    // Constructors
    public OrganizationPerformanceStatsDTO() {
    }

    // Getters and Setters
    public Long getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(Long totalProperties) {
        this.totalProperties = totalProperties;
    }

    public Long getAvailableProperties() {
        return availableProperties;
    }

    public void setAvailableProperties(Long availableProperties) {
        this.availableProperties = availableProperties;
    }

    public Long getSoldProperties() {
        return soldProperties;
    }

    public void setSoldProperties(Long soldProperties) {
        this.soldProperties = soldProperties;
    }

    public Long getRentedProperties() {
        return rentedProperties;
    }

    public void setRentedProperties(Long rentedProperties) {
        this.rentedProperties = rentedProperties;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
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

    public BigDecimal getAverageSurface() {
        return averageSurface;
    }

    public void setAverageSurface(BigDecimal averageSurface) {
        this.averageSurface = averageSurface;
    }

    public Long getNewThisMonth() {
        return newThisMonth;
    }

    public void setNewThisMonth(Long newThisMonth) {
        this.newThisMonth = newThisMonth;
    }

    public Long getNewThisWeek() {
        return newThisWeek;
    }

    public void setNewThisWeek(Long newThisWeek) {
        this.newThisWeek = newThisWeek;
    }

    public Map<String, Long> getByType() {
        return byType;
    }

    public void setByType(Map<String, Long> byType) {
        this.byType = byType;
    }

    public Map<String, Long> getByCity() {
        return byCity;
    }

    public void setByCity(Map<String, Long> byCity) {
        this.byCity = byCity;
    }

    public Map<String, Long> getByStatus() {
        return byStatus;
    }

    public void setByStatus(Map<String, Long> byStatus) {
        this.byStatus = byStatus;
    }

    public List<String> getServedCities() {
        return servedCities;
    }

    public void setServedCities(List<String> servedCities) {
        this.servedCities = servedCities;
    }

    public List<String> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(List<String> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }
}

