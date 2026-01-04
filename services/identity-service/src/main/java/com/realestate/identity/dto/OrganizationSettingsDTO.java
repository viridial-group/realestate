package com.realestate.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class OrganizationSettingsDTO {

    @Size(max = 500)
    private String logoUrl;

    // Adresse
    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 100)
    private String country;

    // Contact
    @Size(max = 20)
    private String phone;

    @Email
    @Size(max = 255)
    private String email;

    // Domaines personnalisés (JSON array string)
    private String customDomains;

    // Quotas (JSON object string)
    private String quotas;

    // Horaires du bureau par défaut (JSON string)
    private String defaultOfficeHours; // JSON pour les horaires du bureau par défaut

    // Getters and Setters
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

