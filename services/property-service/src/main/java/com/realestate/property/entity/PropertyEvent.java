package com.realestate.property.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité pour stocker les événements liés aux propriétés
 * Permet de générer l'historique des statistiques (vues, contacts, favoris, partages)
 */
@Entity
@Table(name = "property_events", indexes = {
    @Index(name = "idx_property_events_property_id", columnList = "property_id"),
    @Index(name = "idx_property_events_created_at", columnList = "created_at"),
    @Index(name = "idx_property_events_type", columnList = "event_type"),
    @Index(name = "idx_property_events_property_date", columnList = "property_id, created_at")
})
public class PropertyEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // VIEW, CONTACT, FAVORITE, SHARE

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id")
    private Long userId; // Utilisateur qui a déclenché l'événement (optionnel)

    @Column(columnDefinition = "JSONB")
    private String metadata; // Métadonnées supplémentaires au format JSON

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", insertable = false, updatable = false)
    private Property property;

    public PropertyEvent() {
    }

    public PropertyEvent(Long propertyId, String eventType, Long userId) {
        this.propertyId = propertyId;
        this.eventType = eventType;
        this.userId = userId;
    }

    public PropertyEvent(Long propertyId, String eventType, Long userId, String metadata) {
        this.propertyId = propertyId;
        this.eventType = eventType;
        this.userId = userId;
        this.metadata = metadata;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    // Constantes pour les types d'événements
    public static final String EVENT_TYPE_VIEW = "VIEW";
    public static final String EVENT_TYPE_CONTACT = "CONTACT";
    public static final String EVENT_TYPE_FAVORITE = "FAVORITE";
    public static final String EVENT_TYPE_SHARE = "SHARE";
}

