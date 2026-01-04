package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO pour créer une entrée d'historique de prix")
public class PriceHistoryCreateDTO {

    @NotNull
    @Schema(description = "ID de la propriété", required = true)
    private Long propertyId;

    @NotNull
    @Schema(description = "Nouveau prix", required = true)
    private BigDecimal price;

    @Schema(description = "Devise (par défaut: EUR)")
    private String currency = "EUR";

    @Schema(description = "Date du changement (par défaut: maintenant)")
    private LocalDateTime changeDate;

    @Schema(description = "Raison du changement")
    private String changeReason;

    @Schema(description = "Source du changement (MANUAL, AUTO, IMPORT)")
    private String source = "MANUAL";

    // Constructors
    public PriceHistoryCreateDTO() {
    }

    // Getters and Setters
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

