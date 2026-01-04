package com.realestate.property.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "advertisement_impressions", indexes = {
    @Index(name = "idx_ad_impression_ad", columnList = "advertisement_id"),
    @Index(name = "idx_ad_impression_date", columnList = "impressed_at"),
    @Index(name = "idx_ad_impression_ip", columnList = "ip_address")
})
public class AdvertisementImpression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advertisement_id", nullable = false)
    private Long advertisementId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "property_id")
    private Long propertyId; // ID de la propriété sur laquelle l'annonce était affichée

    @Column(name = "page_type", length = 50)
    private String pageType; // HOME, SEARCH, PROPERTY_DETAIL, etc.

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @CreationTimestamp
    @Column(name = "impressed_at", nullable = false, updatable = false)
    private LocalDateTime impressedAt;

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

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
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

    public LocalDateTime getImpressedAt() {
        return impressedAt;
    }

    public void setImpressedAt(LocalDateTime impressedAt) {
        this.impressedAt = impressedAt;
    }
}

