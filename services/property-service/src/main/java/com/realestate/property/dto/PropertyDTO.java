package com.realestate.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PropertyDTO {

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String reference;

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotBlank
    @Size(max = 50)
    private String type;

    @NotBlank
    @Size(max = 50)
    private String status = "DRAFT";

    @NotNull
    private BigDecimal price;

    @Size(max = 3)
    private String currency = "EUR";

    private BigDecimal surface;
    private Integer rooms;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer fullBathrooms;
    
    // Appliances
    private String appliancesIncluded; // JSON array
    private String laundryLocation; // Inside, Outside, None
    
    // Interior area
    private BigDecimal totalStructureArea;
    private BigDecimal totalInteriorLivableArea;
    
    // Video & virtual tour
    private String virtualTourUrl;
    
    // Parking
    private String parkingFeatures; // JSON array
    private Boolean hasGarage;
    
    // Accessibility
    private String accessibilityFeatures; // JSON array
    
    // Features
    private String patioPorch;
    private String exteriorFeatures; // JSON array
    
    // Details
    private String specialConditions; // Resale, New Construction, etc.
    
    // Construction
    private String homeType; // Condo, House, Townhouse, etc.
    private String propertySubtype; // Condominium, Single Family, etc.
    private String condition; // New, Good, Fair, etc.
    private Integer yearBuilt;
    
    // Community & HOA
    private String subdivision;
    private Boolean hasHOA;
    private String hoaAmenities; // JSON array
    private String hoaServices; // JSON array
    private BigDecimal hoaFee;
    private String hoaFeeFrequency; // monthly, quarterly, annually
    
    // Location
    private String region;
    
    // Financial & listing details
    private BigDecimal pricePerSquareFoot;
    private LocalDate dateOnMarket;
    
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long organizationId;
    private String organizationName; // Enriched field
    private Long assignedUserId;
    private String assignedUserName; // Enriched field (firstName + lastName)
    private String metadata; // JSON
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public PropertyDTO() {
    }

    public PropertyDTO(Long id, String reference, String title, String type) {
        this.id = id;
        this.reference = reference;
        this.title = title;
        this.type = type;
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

    public Integer getFullBathrooms() {
        return fullBathrooms;
    }

    public void setFullBathrooms(Integer fullBathrooms) {
        this.fullBathrooms = fullBathrooms;
    }

    public String getAppliancesIncluded() {
        return appliancesIncluded;
    }

    public void setAppliancesIncluded(String appliancesIncluded) {
        this.appliancesIncluded = appliancesIncluded;
    }

    public String getLaundryLocation() {
        return laundryLocation;
    }

    public void setLaundryLocation(String laundryLocation) {
        this.laundryLocation = laundryLocation;
    }

    public BigDecimal getTotalStructureArea() {
        return totalStructureArea;
    }

    public void setTotalStructureArea(BigDecimal totalStructureArea) {
        this.totalStructureArea = totalStructureArea;
    }

    public BigDecimal getTotalInteriorLivableArea() {
        return totalInteriorLivableArea;
    }

    public void setTotalInteriorLivableArea(BigDecimal totalInteriorLivableArea) {
        this.totalInteriorLivableArea = totalInteriorLivableArea;
    }

    public String getVirtualTourUrl() {
        return virtualTourUrl;
    }

    public void setVirtualTourUrl(String virtualTourUrl) {
        this.virtualTourUrl = virtualTourUrl;
    }

    public String getParkingFeatures() {
        return parkingFeatures;
    }

    public void setParkingFeatures(String parkingFeatures) {
        this.parkingFeatures = parkingFeatures;
    }

    public Boolean getHasGarage() {
        return hasGarage;
    }

    public void setHasGarage(Boolean hasGarage) {
        this.hasGarage = hasGarage;
    }

    public String getAccessibilityFeatures() {
        return accessibilityFeatures;
    }

    public void setAccessibilityFeatures(String accessibilityFeatures) {
        this.accessibilityFeatures = accessibilityFeatures;
    }

    public String getPatioPorch() {
        return patioPorch;
    }

    public void setPatioPorch(String patioPorch) {
        this.patioPorch = patioPorch;
    }

    public String getExteriorFeatures() {
        return exteriorFeatures;
    }

    public void setExteriorFeatures(String exteriorFeatures) {
        this.exteriorFeatures = exteriorFeatures;
    }

    public String getSpecialConditions() {
        return specialConditions;
    }

    public void setSpecialConditions(String specialConditions) {
        this.specialConditions = specialConditions;
    }

    public String getHomeType() {
        return homeType;
    }

    public void setHomeType(String homeType) {
        this.homeType = homeType;
    }

    public String getPropertySubtype() {
        return propertySubtype;
    }

    public void setPropertySubtype(String propertySubtype) {
        this.propertySubtype = propertySubtype;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivision) {
        this.subdivision = subdivision;
    }

    public Boolean getHasHOA() {
        return hasHOA;
    }

    public void setHasHOA(Boolean hasHOA) {
        this.hasHOA = hasHOA;
    }

    public String getHoaAmenities() {
        return hoaAmenities;
    }

    public void setHoaAmenities(String hoaAmenities) {
        this.hoaAmenities = hoaAmenities;
    }

    public String getHoaServices() {
        return hoaServices;
    }

    public void setHoaServices(String hoaServices) {
        this.hoaServices = hoaServices;
    }

    public BigDecimal getHoaFee() {
        return hoaFee;
    }

    public void setHoaFee(BigDecimal hoaFee) {
        this.hoaFee = hoaFee;
    }

    public String getHoaFeeFrequency() {
        return hoaFeeFrequency;
    }

    public void setHoaFeeFrequency(String hoaFeeFrequency) {
        this.hoaFeeFrequency = hoaFeeFrequency;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public BigDecimal getPricePerSquareFoot() {
        return pricePerSquareFoot;
    }

    public void setPricePerSquareFoot(BigDecimal pricePerSquareFoot) {
        this.pricePerSquareFoot = pricePerSquareFoot;
    }

    public LocalDate getDateOnMarket() {
        return dateOnMarket;
    }

    public void setDateOnMarket(LocalDate dateOnMarket) {
        this.dateOnMarket = dateOnMarket;
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

