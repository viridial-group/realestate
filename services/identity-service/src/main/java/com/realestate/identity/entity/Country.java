package com.realestate.identity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "countries", indexes = {
    @Index(name = "idx_country_code", columnList = "code", unique = true),
    @Index(name = "idx_country_name", columnList = "name")
})
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 2)
    @Column(nullable = false, unique = true, length = 2)
    private String code; // Code ISO 3166-1 alpha-2 (ex: FR, US, GB)

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name; // Nom du pays (ex: France, United States)

    @Size(max = 3)
    @Column(length = 3)
    private String code3; // Code ISO 3166-1 alpha-3 (ex: FRA, USA, GBR)

    @Size(max = 10)
    private String phoneCode; // Code téléphonique international (ex: +33, +1)

    @Size(max = 10)
    private String currency; // Code devise (ex: EUR, USD, GBP)

    @Size(max = 10)
    private String currencySymbol; // Symbole devise (ex: €, $, £)

    @Size(max = 50)
    private String timezone; // Fuseau horaire principal (ex: Europe/Paris, America/New_York)

    @Column(columnDefinition = "TEXT")
    private String flagEmoji; // Emoji du drapeau (ex: 🇫🇷, 🇺🇸)

    @Column(columnDefinition = "TEXT")
    private String description; // Description du pays

    // Coordonnées géographiques du centre du pays
    @Column(columnDefinition = "DECIMAL(10, 7)")
    private Double latitude; // Latitude du centre

    @Column(columnDefinition = "DECIMAL(10, 7)")
    private Double longitude; // Longitude du centre

    // Données importantes (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "important_data", columnDefinition = "jsonb", nullable = true)
    private String importantData; // JSON: {"population": 67000000, "area": 643801, "languages": ["fr"], "capital": "Paris"}

    @Column(nullable = false)
    private Boolean active = true; // Pays actif ou non

    @Column(nullable = false)
    private Integer displayOrder = 0; // Ordre d'affichage

    // Relations
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<City> cities = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Country() {
    }

    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode3() {
        return code3;
    }

    public void setCode3(String code3) {
        this.code3 = code3;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getFlagEmoji() {
        return flagEmoji;
    }

    public void setFlagEmoji(String flagEmoji) {
        this.flagEmoji = flagEmoji;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
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

