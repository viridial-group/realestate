package com.realestate.identity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "cities", indexes = {
    @Index(name = "idx_city_name", columnList = "name"),
    @Index(name = "idx_city_country", columnList = "country_id"),
    @Index(name = "idx_city_postal_code", columnList = "postal_code"),
    @Index(name = "idx_city_active", columnList = "active")
})
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name; // Nom de la ville (ex: Paris, New York)

    @Size(max = 20)
    @Column(name = "postal_code")
    private String postalCode; // Code postal (ex: 75001, 10001)

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    // Coordonnées géographiques
    @Column(columnDefinition = "DECIMAL(10, 7)")
    private Double latitude; // Latitude

    @Column(columnDefinition = "DECIMAL(10, 7)")
    private Double longitude; // Longitude

    @Size(max = 100)
    private String region; // Région/État (ex: Île-de-France, New York)

    @Size(max = 100)
    private String department; // Département (pour la France) ou équivalent

    @Size(max = 50)
    private String timezone; // Fuseau horaire (ex: Europe/Paris, America/New_York)

    @Column(columnDefinition = "TEXT")
    private String description; // Description de la ville

    // Données importantes (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "important_data", columnDefinition = "jsonb", nullable = true)
    private String importantData; // JSON: {"population": 2161000, "area": 105.4, "density": 20499, "tourist_attractions": ["Eiffel Tower", "Louvre"]}

    @Column(nullable = false)
    private Boolean active = true; // Ville active ou non

    @Column(nullable = false)
    private Integer displayOrder = 0; // Ordre d'affichage

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public City() {
    }

    public City(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImportantData() {
        return importantData;
    }

    public void setImportantData(String importantData) {
        this.importantData = importantData;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

