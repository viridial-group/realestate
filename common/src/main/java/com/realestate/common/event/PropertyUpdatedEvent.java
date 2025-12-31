package com.realestate.common.event;

import java.math.BigDecimal;

/**
 * Event published when a property is updated.
 */
public class PropertyUpdatedEvent extends BaseEvent {
    
    private Long propertyId;
    private String propertyReference;
    private String propertyTitle;
    private String propertyType;
    private BigDecimal price;
    private String status;
    private String city;
    private String country;
    
    public PropertyUpdatedEvent() {
        super();
    }
    
    public PropertyUpdatedEvent(Long organizationId, Long userId, Long propertyId, 
                               String propertyReference, String propertyTitle, 
                               String propertyType, BigDecimal price, String status,
                               String city, String country) {
        super(organizationId, userId);
        this.propertyId = propertyId;
        this.propertyReference = propertyReference;
        this.propertyTitle = propertyTitle;
        this.propertyType = propertyType;
        this.price = price;
        this.status = status;
        this.city = city;
        this.country = country;
    }
    
    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public String getPropertyReference() {
        return propertyReference;
    }
    
    public void setPropertyReference(String propertyReference) {
        this.propertyReference = propertyReference;
    }
    
    public String getPropertyTitle() {
        return propertyTitle;
    }
    
    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }
    
    public String getPropertyType() {
        return propertyType;
    }
    
    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
}

