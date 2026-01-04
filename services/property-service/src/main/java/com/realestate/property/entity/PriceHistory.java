package com.realestate.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history", indexes = {
    @Index(name = "idx_price_history_property", columnList = "property_id"),
    @Index(name = "idx_price_history_date", columnList = "change_date"),
    @Index(name = "idx_price_history_property_date", columnList = "property_id, change_date")
})
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(length = 3)
    private String currency = "EUR";

    @NotNull
    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;

    @Column(name = "change_reason", length = 500)
    private String changeReason; // Raison du changement (ex: "Market adjustment", "Renovation", "Price reduction", etc.)

    @Column(name = "created_by")
    private Long createdBy; // Utilisateur qui a effectué le changement

    @Column(name = "source")
    private String source; // Source du changement: "MANUAL", "AUTO", "IMPORT", etc.

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public PriceHistory() {
    }

    public PriceHistory(Long propertyId, BigDecimal price, LocalDateTime changeDate) {
        this.propertyId = propertyId;
        this.price = price;
        this.changeDate = changeDate;
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

