package com.realestate.property.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdvertisementStatsDTO {
    private Long advertisementId;
    private String title;
    private Long impressions;
    private Long clicks;
    private Long conversions;
    private BigDecimal totalSpent;
    private Double clickThroughRate; // CTR
    private Double conversionRate; // CR
    private BigDecimal costPerClick; // CPC
    private BigDecimal costPerConversion; // CPA
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;

    // Getters and Setters
    public Long getAdvertisementId() {
        return advertisementId;
    }

    public void setAdvertisementId(Long advertisementId) {
        this.advertisementId = advertisementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getImpressions() {
        return impressions;
    }

    public void setImpressions(Long impressions) {
        this.impressions = impressions;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public Long getConversions() {
        return conversions;
    }

    public void setConversions(Long conversions) {
        this.conversions = conversions;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Double getClickThroughRate() {
        return clickThroughRate;
    }

    public void setClickThroughRate(Double clickThroughRate) {
        this.clickThroughRate = clickThroughRate;
    }

    public Double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public BigDecimal getCostPerClick() {
        return costPerClick;
    }

    public void setCostPerClick(BigDecimal costPerClick) {
        this.costPerClick = costPerClick;
    }

    public BigDecimal getCostPerConversion() {
        return costPerConversion;
    }

    public void setCostPerConversion(BigDecimal costPerConversion) {
        this.costPerConversion = costPerConversion;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

