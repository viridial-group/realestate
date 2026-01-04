package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "DTO pour créer une alerte de prix")
public class PriceAlertCreateDTO {

    @NotNull
    @Schema(description = "ID de la propriété", required = true)
    private Long propertyId;

    @NotNull
    @Schema(description = "Type d'alerte (PRICE_DROP, PRICE_INCREASE, PERCENTAGE_DROP, PERCENTAGE_INCREASE)", required = true)
    private String alertType;

    @Schema(description = "Prix cible (requis si alertType est PRICE_DROP ou PRICE_INCREASE)")
    private BigDecimal targetPrice;

    @Schema(description = "Seuil de pourcentage (requis si alertType est PERCENTAGE_DROP ou PERCENTAGE_INCREASE)")
    private Double percentageThreshold;

    @Schema(description = "Notification par email (par défaut: true)")
    private Boolean emailNotification = true;

    @Schema(description = "Notification in-app (par défaut: true)")
    private Boolean inAppNotification = true;

    // Constructors
    public PriceAlertCreateDTO() {
    }

    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
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
}

