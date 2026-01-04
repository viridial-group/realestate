package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Organisation avec ses statistiques d'avis")
public class OrganizationWithReviewsDTO {

    @Schema(description = "ID de l'organisation")
    private Long id;

    @Schema(description = "Nom de l'organisation")
    private String name;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Logo URL")
    private String logoUrl;

    @Schema(description = "Adresse")
    private String address;

    @Schema(description = "Ville")
    private String city;

    @Schema(description = "Code postal")
    private String postalCode;

    @Schema(description = "Pays")
    private String country;

    @Schema(description = "Téléphone")
    private String phone;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Domaine/URL du site web")
    private String domain;

    @Schema(description = "Horaires du bureau par défaut (JSON)")
    private String defaultOfficeHours;

    @Schema(description = "Statistiques des avis")
    private OrganizationReviewStatsDTO reviewStats;

    @Schema(description = "Nombre de propriétés")
    private Long propertyCount;

    // Constructors
    public OrganizationWithReviewsDTO() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDefaultOfficeHours() {
        return defaultOfficeHours;
    }

    public void setDefaultOfficeHours(String defaultOfficeHours) {
        this.defaultOfficeHours = defaultOfficeHours;
    }

    public OrganizationReviewStatsDTO getReviewStats() {
        return reviewStats;
    }

    public void setReviewStats(OrganizationReviewStatsDTO reviewStats) {
        this.reviewStats = reviewStats;
    }

    public Long getPropertyCount() {
        return propertyCount;
    }

    public void setPropertyCount(Long propertyCount) {
        this.propertyCount = propertyCount;
    }
}

