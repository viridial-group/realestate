package com.realestate.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdvertisementCreateDTO {
    @NotNull(message = "Organization ID is required")
    private Long organizationId;

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    @Size(max = 500)
    private String imageUrl;

    @Size(max = 500)
    private String linkUrl;

    @NotBlank(message = "Ad type is required")
    @Size(max = 50)
    private String adType; // BANNER, SIDEBAR, INLINE, POPUP, SPONSORED_PROPERTY

    @NotBlank(message = "Position is required")
    @Size(max = 50)
    private String position; // TOP, BOTTOM, SIDEBAR_LEFT, SIDEBAR_RIGHT, INLINE, HEADER, FOOTER

    private String status = "DRAFT"; // DRAFT, ACTIVE, PAUSED, EXPIRED, ARCHIVED

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BigDecimal budget;

    private BigDecimal dailyBudget;

    private BigDecimal costPerClick;

    private BigDecimal costPerImpression;

    private String targetLocations; // JSON

    private String targetPropertyTypes; // JSON

    private String targetTransactionTypes; // JSON

    private Integer maxImpressionsPerDay;

    private Integer priority = 0;

    private Boolean active = true;

    private Long createdBy;

    // Getters and Setters
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}

