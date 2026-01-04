package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO pour l'historique des prix d'une propriété")
public class PriceHistoryDTO {

    @Schema(description = "ID de l'entrée d'historique")
    private Long id;

    @Schema(description = "ID de la propriété")
    private Long propertyId;

    @Schema(description = "Prix")
    private BigDecimal price;

    @Schema(description = "Devise")
    private String currency;

    @Schema(description = "Date du changement de prix")
    private LocalDateTime changeDate;

    @Schema(description = "Raison du changement")
    private String changeReason;

    @Schema(description = "ID de l'utilisateur qui a effectué le changement")
    private Long createdBy;

    @Schema(description = "Source du changement (MANUAL, AUTO, IMPORT)")
    private String source;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de mise à jour")
    private LocalDateTime updatedAt;

    // Constructors
    public PriceHistoryDTO() {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

