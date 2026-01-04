package com.realestate.property.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "advertisement_clicks", indexes = {
    @Index(name = "idx_ad_click_ad", columnList = "advertisement_id"),
    @Index(name = "idx_ad_click_date", columnList = "clicked_at"),
    @Index(name = "idx_ad_click_ip", columnList = "ip_address")
})
public class AdvertisementClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advertisement_id", nullable = false)
    private Long advertisementId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress; // IPv4 ou IPv6

    @Column(name = "user_agent", length = 500)
    private String userAgent; // User agent du navigateur

    @Column(name = "referrer", length = 500)
    private String referrer; // Page d'origine

    @Column(name = "property_id")
    private Long propertyId; // ID de la propriété sur laquelle l'annonce était affichée (si applicable)

    @Column(name = "city")
    private String city; // Ville où l'utilisateur se trouve

    @Column(name = "postal_code")
    private String postalCode; // Code postal

    @CreationTimestamp
    @Column(name = "clicked_at", nullable = false, updatable = false)
    private LocalDateTime clickedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdvertisementId() {
        return advertisementId;
    }

    public void setAdvertisementId(Long advertisementId) {
        this.advertisementId = advertisementId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
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

    public LocalDateTime getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }
}

