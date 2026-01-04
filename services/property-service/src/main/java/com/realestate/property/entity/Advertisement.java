package com.realestate.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements", indexes = {
    @Index(name = "idx_ad_organization", columnList = "organization_id"),
    @Index(name = "idx_ad_status", columnList = "status"),
    @Index(name = "idx_ad_active", columnList = "active"),
    @Index(name = "idx_ad_type", columnList = "ad_type"),
    @Index(name = "idx_ad_start_end", columnList = "start_date, end_date")
})
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "organization_id", nullable = false)
    private Long organizationId; // Organisation qui crée l'annonce

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title; // Titre de l'annonce

    @Size(max = 1000)
    private String description; // Description de l'annonce

    @Size(max = 500)
    @Column(name = "image_url")
    private String imageUrl; // URL de l'image de l'annonce

    @Size(max = 500)
    @Column(name = "link_url")
    private String linkUrl; // URL de destination du clic

    @Size(max = 50)
    @Column(name = "ad_type", nullable = false)
    private String adType; // BANNER, SIDEBAR, INLINE, POPUP, SPONSORED_PROPERTY

    @Size(max = 50)
    @Column(name = "position", nullable = false)
    private String position; // TOP, BOTTOM, SIDEBAR_LEFT, SIDEBAR_RIGHT, INLINE, HEADER, FOOTER

    @Size(max = 50)
    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT, ACTIVE, PAUSED, EXPIRED, ARCHIVED

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // Date de début de l'annonce

    @Column(name = "end_date")
    private LocalDateTime endDate; // Date de fin de l'annonce (null = pas de fin)

    @Column(name = "budget", precision = 15, scale = 2)
    private BigDecimal budget; // Budget total de la campagne

    @Column(name = "daily_budget", precision = 15, scale = 2)
    private BigDecimal dailyBudget; // Budget quotidien

    @Column(name = "cost_per_click", precision = 10, scale = 2)
    private BigDecimal costPerClick; // Coût par clic (CPC)

    @Column(name = "cost_per_impression", precision = 10, scale = 2)
    private BigDecimal costPerImpression; // Coût par mille impressions (CPM)

    // Ciblage géographique (JSON)
    @Column(name = "target_locations", columnDefinition = "TEXT")
    private String targetLocations; // JSON: {"cities": ["Paris", "Lyon"], "postalCodes": ["75001"], "regions": ["Île-de-France"]}

    // Ciblage par type de propriété (JSON)
    @Column(name = "target_property_types", columnDefinition = "TEXT")
    private String targetPropertyTypes; // JSON: ["APARTMENT", "HOUSE", "COMMERCIAL"]

    // Ciblage par transaction (JSON)
    @Column(name = "target_transaction_types", columnDefinition = "TEXT")
    private String targetTransactionTypes; // JSON: ["RENT", "SALE"]

    // Statistiques
    @Column(name = "impressions", nullable = false)
    private Long impressions = 0L; // Nombre d'impressions

    @Column(name = "clicks", nullable = false)
    private Long clicks = 0L; // Nombre de clics

    @Column(name = "conversions", nullable = false)
    private Long conversions = 0L; // Nombre de conversions (contacts, visites, etc.)

    @Column(name = "total_spent", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalSpent = BigDecimal.ZERO; // Montant total dépensé

    // Paramètres d'affichage
    @Column(name = "max_impressions_per_day")
    private Integer maxImpressionsPerDay; // Limite d'impressions par jour

    @Column(name = "priority", nullable = false)
    private Integer priority = 0; // Priorité d'affichage (plus élevé = affiché en premier)

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy; // ID de l'utilisateur qui a créé l'annonce

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
}

