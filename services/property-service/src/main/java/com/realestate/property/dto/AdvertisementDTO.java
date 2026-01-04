package com.realestate.property.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdvertisementDTO {
    private Long id;
    private Long organizationId;
    private String organizationName;
    private String title;
    private String description;
    private String imageUrl;
    private String linkUrl;
    private String adType;
    private String position;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal budget;
    private BigDecimal dailyBudget;
    private BigDecimal costPerClick;
    private BigDecimal costPerImpression;
    private String targetLocations;
    private String targetPropertyTypes;
    private String targetTransactionTypes;
    private Long impressions;
    private Long clicks;
    private Long conversions;
    private BigDecimal totalSpent;
    private Integer maxImpressionsPerDay;
    private Integer priority;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;

    // Calculated fields
    private Double clickThroughRate; // CTR = (clicks / impressions) * 100
    private Double conversionRate; // CR = (conversions / clicks) * 100
    private BigDecimal costPerConversion; // CPC = totalSpent / conversions

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(BigDecimal dailyBudget) {
        this.dailyBudget = dailyBudget;
    }

    public BigDecimal getCostPerClick() {
        return costPerClick;
    }

    public void setCostPerClick(BigDecimal costPerClick) {
        this.costPerClick = costPerClick;
    }

    public BigDecimal getCostPerImpression() {
        return costPerImpression;
    }

    public void setCostPerImpression(BigDecimal costPerImpression) {
        this.costPerImpression = costPerImpression;
    }

    public String getTargetLocations() {
        return targetLocations;
    }

    public void setTargetLocations(String targetLocations) {
        this.targetLocations = targetLocations;
    }

    public String getTargetPropertyTypes() {
        return targetPropertyTypes;
    }

    public void setTargetPropertyTypes(String targetPropertyTypes) {
        this.targetPropertyTypes = targetPropertyTypes;
    }

    public String getTargetTransactionTypes() {
        return targetTransactionTypes;
    }

    public void setTargetTransactionTypes(String targetTransactionTypes) {
        this.targetTransactionTypes = targetTransactionTypes;
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

    public Integer getMaxImpressionsPerDay() {
        return maxImpressionsPerDay;
    }

    public void setMaxImpressionsPerDay(Integer maxImpressionsPerDay) {
        this.maxImpressionsPerDay = maxImpressionsPerDay;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

    public BigDecimal getCostPerConversion() {
        return costPerConversion;
    }

    public void setCostPerConversion(BigDecimal costPerConversion) {
        this.costPerConversion = costPerConversion;
    }
}

