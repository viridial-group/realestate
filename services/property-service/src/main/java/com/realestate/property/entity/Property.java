package com.realestate.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "properties", indexes = {
    @Index(name = "idx_property_org", columnList = "organization_id"),
    @Index(name = "idx_property_assigned_user", columnList = "assigned_user_id"),
    @Index(name = "idx_property_reference", columnList = "reference", unique = true),
    @Index(name = "idx_property_status", columnList = "status"),
    @Index(name = "idx_property_type", columnList = "type"),
    @Index(name = "idx_property_city", columnList = "city")
})
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String reference; // Référence unique de la propriété

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String type; // APARTMENT, HOUSE, LAND, COMMERCIAL, etc.

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT, PUBLISHED, SOLD, RENTED, ARCHIVED

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Size(max = 3)
    @Column(length = 3)
    private String currency = "EUR"; // EUR, USD, etc.

    @Column(precision = 10, scale = 2)
    private BigDecimal surface; // Surface en m²

    private Integer rooms; // Nombre de pièces

    private Integer bedrooms; // Nombre de chambres

    private Integer bathrooms; // Nombre de salles de bain

    @Size(max = 100)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 100)
    private String country;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "team_id")
    private Long teamId; // Optionnel

    @Column(name = "assigned_user_id")
    private Long assignedUserId; // Utilisateur assigné à la propriété

    @Column(nullable = false)
    private Boolean active = true;

    // Métadonnées JSON pour caractéristiques supplémentaires
    @Column(columnDefinition = "TEXT")
    private String features; // JSON pour stocker des caractéristiques spécifiques

    // Relations
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PropertyAccess> accesses = new HashSet<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PropertyFeature> propertyFeatures = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Property() {
    }

    public Property(String reference, String title, String type, BigDecimal price, Long organizationId) {
        this.reference = reference;
        this.title = title;
        this.type = type;
        this.price = price;
        this.organizationId = organizationId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public BigDecimal getSurface() {
        return surface;
    }

    public void setSurface(BigDecimal surface) {
        this.surface = surface;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public Set<PropertyAccess> getAccesses() {
        return accesses;
    }

    public void setAccesses(Set<PropertyAccess> accesses) {
        this.accesses = accesses;
    }

    public Set<PropertyFeature> getPropertyFeatures() {
        return propertyFeatures;
    }

    public void setPropertyFeatures(Set<PropertyFeature> propertyFeatures) {
        this.propertyFeatures = propertyFeatures;
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

