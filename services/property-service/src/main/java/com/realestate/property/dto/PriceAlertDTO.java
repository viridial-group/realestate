package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO pour une alerte de prix")
public class PriceAlertDTO {

    @Schema(description = "ID de l'alerte")
    private Long id;

    @Schema(description = "ID de la propriété")
    private Long propertyId;

    @Schema(description = "Titre de la propriété (pour affichage)")
    private String propertyTitle;

    @Schema(description = "ID de l'utilisateur")
    private Long userId;

    @Schema(description = "Type d'alerte (PRICE_DROP, PRICE_INCREASE, PERCENTAGE_DROP, etc.)")
    private String alertType;

    @Schema(description = "Prix cible")
    private BigDecimal targetPrice;

    @Schema(description = "Seuil de pourcentage")
    private Double percentageThreshold;

    @Schema(description = "Indique si l'alerte est basée sur un pourcentage")
    private Boolean isPercentage;

    @Schema(description = "Indique si l'alerte a déjà été notifiée")
    private Boolean notified;

    @Schema(description = "Date de la dernière notification")
    private LocalDateTime notifiedAt;

    @Schema(description = "Alerte active")
    private Boolean active;

    @Schema(description = "Notification par email activée")
    private Boolean emailNotification;

    @Schema(description = "Notification in-app activée")
    private Boolean inAppNotification;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de mise à jour")
    private LocalDateTime updatedAt;

    // Constructors
    public PriceAlertDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public Double getPercentageThreshold() {
        return percentageThreshold;
    }

    public void setPercentageThreshold(Double percentageThreshold) {
        this.percentageThreshold = percentageThreshold;
    }

    public Boolean getIsPercentage() {
        return isPercentage;
    }

    public void setIsPercentage(Boolean isPercentage) {
        this.isPercentage = isPercentage;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public LocalDateTime getNotifiedAt() {
        return notifiedAt;
    }

    public void setNotifiedAt(LocalDateTime notifiedAt) {
        this.notifiedAt = notifiedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(Boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    public Boolean getInAppNotification() {
        return inAppNotification;
    }

    public void setInAppNotification(Boolean inAppNotification) {
        this.inAppNotification = inAppNotification;
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
}

