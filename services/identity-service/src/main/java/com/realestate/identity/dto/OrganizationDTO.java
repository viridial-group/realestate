package com.realestate.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class OrganizationDTO {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String description;

    @Size(max = 100)
    private String domain;

    private Boolean active = true;
    private Long parentId; // ID du parent (évite lazy loading)
    
    // Paramètres d'organisation
    private String logoUrl;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String customDomains; // JSON array
    private String quotas; // JSON object
    private String defaultOfficeHours; // JSON pour les horaires du bureau par défaut
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public OrganizationDTO() {
    }

    public OrganizationDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public String getCustomDomains() {
        return customDomains;
    }

    public void setCustomDomains(String customDomains) {
        this.customDomains = customDomains;
    }

    public String getQuotas() {
        return quotas;
    }

    public void setQuotas(String quotas) {
        this.quotas = quotas;
    }

    public String getDefaultOfficeHours() {
        return defaultOfficeHours;
    }

    public void setDefaultOfficeHours(String defaultOfficeHours) {
        this.defaultOfficeHours = defaultOfficeHours;
    }
}

