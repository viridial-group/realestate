package com.realestate.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_alerts", indexes = {
    @Index(name = "idx_price_alert_property", columnList = "property_id"),
    @Index(name = "idx_price_alert_user", columnList = "user_id"),
    @Index(name = "idx_price_alert_active", columnList = "active"),
    @Index(name = "idx_price_alert_property_user", columnList = "property_id, user_id")
})
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "alert_type", length = 50, nullable = false)
    private String alertType; // PRICE_DROP, PRICE_INCREASE, PRICE_CHANGE, PERCENTAGE_DROP, PERCENTAGE_INCREASE

    @Column(name = "target_price", precision = 15, scale = 2)
    private BigDecimal targetPrice; // Prix cible pour l'alerte

    @Column(name = "percentage_threshold")
    private Double percentageThreshold; // Seuil de pourcentage (ex: 5.0 pour 5%)

    @Column(name = "is_percentage", nullable = false)
    private Boolean isPercentage = false; // true si l'alerte est basée sur un pourcentage

    @Column(name = "notified", nullable = false)
    private Boolean notified = false; // Indique si l'alerte a déjà été notifiée

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt; // Date de la dernière notification

    @Column(name = "active", nullable = false)
    private Boolean active = true; // Pour activer/désactiver l'alerte

    @Column(name = "email_notification", nullable = false)
    private Boolean emailNotification = true; // Envoyer une notification par email

    @Column(name = "in_app_notification", nullable = false)
    private Boolean inAppNotification = true; // Envoyer une notification in-app

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public PriceAlert() {
    }

    public PriceAlert(Long propertyId, Long userId, String alertType) {
        this.propertyId = propertyId;
        this.userId = userId;
        this.alertType = alertType;
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

