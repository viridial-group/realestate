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

    @Size(max = 50)
    @Column(name = "transaction_type")
    private String transactionType; // RENT, SALE - Type de transaction (Location ou Vente)

    @Size(max = 255)
    @Column(name = "slug", unique = true)
    private String slug; // Slug SEO-friendly pour les URLs (ex: appartement-paris-3-pieces-123)

    // SEO Meta Tags
    @Size(max = 255)
    @Column(name = "meta_title")
    private String metaTitle; // Titre SEO personnalisé (optionnel, sinon utilise title)

    @Size(max = 500)
    @Column(name = "meta_description")
    private String metaDescription; // Description SEO personnalisée (optionnel, sinon utilise description)

    @Size(max = 500)
    @Column(name = "meta_keywords")
    private String metaKeywords; // Mots-clés SEO séparés par des virgules

    @Size(max = 500)
    @Column(name = "og_image")
    private String ogImage; // Image pour Open Graph (optionnel, sinon utilise première image)

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

    private Integer fullBathrooms; // Nombre de salles de bain complètes

    // Appliances
    @Column(name = "appliances_included", columnDefinition = "TEXT")
    private String appliancesIncluded; // JSON array: ["Dishwasher", "Refrigerator", etc.]

    @Size(max = 50)
    @Column(name = "laundry_location")
    private String laundryLocation; // Inside, Outside, None

    // Interior area
    @Column(name = "total_structure_area", precision = 10, scale = 2)
    private BigDecimal totalStructureArea; // Total structure area in sqft or m²

    @Column(name = "total_interior_livable_area", precision = 10, scale = 2)
    private BigDecimal totalInteriorLivableArea; // Total interior livable area in sqft or m²

    // Video & virtual tour
    @Size(max = 500)
    @Column(name = "virtual_tour_url")
    private String virtualTourUrl; // URL to virtual tour

    // Parking
    @Column(name = "parking_features", columnDefinition = "TEXT")
    private String parkingFeatures; // JSON array: ["Garage", "Street", etc.]

    @Column(name = "has_garage")
    private Boolean hasGarage;

    // Accessibility
    @Column(name = "accessibility_features", columnDefinition = "TEXT")
    private String accessibilityFeatures; // JSON array: ["Wheelchair Access", etc.]

    // Features
    @Size(max = 200)
    @Column(name = "patio_porch")
    private String patioPorch; // Other, Patio, Porch, etc.

    @Column(name = "exterior_features", columnDefinition = "TEXT")
    private String exteriorFeatures; // JSON array: ["Courtyard", "Balcony", etc.]

    // Details
    @Size(max = 100)
    @Column(name = "special_conditions")
    private String specialConditions; // Resale, New Construction, Foreclosure, etc.

    // Construction
    @Size(max = 50)
    @Column(name = "home_type")
    private String homeType; // Condo, House, Townhouse, etc.

    @Size(max = 50)
    @Column(name = "property_subtype")
    private String propertySubtype; // Condominium, Single Family, etc.

    @Size(max = 50)
    private String condition; // New, Good, Fair, Needs Renovation, etc.

    @Column(name = "year_built")
    private Integer yearBuilt;

    // Community & HOA
    @Size(max = 200)
    private String subdivision; // Subdivision name

    @Column(name = "has_hoa")
    private Boolean hasHOA;

    @Column(name = "hoa_amenities", columnDefinition = "TEXT")
    private String hoaAmenities; // JSON array: ["Laundry", "Elevator(s)", "Pool", etc.]

    @Column(name = "hoa_services", columnDefinition = "TEXT")
    private String hoaServices; // JSON array: ["Maintenance", "Security", etc.]

    @Column(name = "hoa_fee", precision = 10, scale = 2)
    private BigDecimal hoaFee;

    @Size(max = 20)
    @Column(name = "hoa_fee_frequency")
    private String hoaFeeFrequency; // monthly, quarterly, annually

    // Location
    @Size(max = 100)
    private String region; // Brooklyn, Manhattan, etc.

    // Financial & listing details
    @Column(name = "price_per_square_foot", precision = 10, scale = 2)
    private BigDecimal pricePerSquareFoot;

    @Column(name = "date_on_market")
    private java.time.LocalDate dateOnMarket;

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

    @Column(name = "created_by", nullable = false)
    private Long createdBy; // Utilisateur qui a créé la propriété

    @Column(nullable = false)
    private Boolean active = true;

    // Métadonnées JSON pour caractéristiques supplémentaires
    @Column(columnDefinition = "TEXT")
    private String features; // JSON pour stocker des caractéristiques spécifiques

    // Zillow-inspired fields
    @Column(name = "pet_friendly")
    private Boolean petFriendly; // Accepte les animaux de compagnie

    @Size(max = 500)
    @Column(name = "special_offer")
    private String specialOffer; // Offre spéciale (ex: "3 Months Free Rent")

    @Column(name = "office_hours", columnDefinition = "TEXT")
    private String officeHours; // JSON pour les horaires du bureau

    @Size(max = 100)
    private String neighborhood; // Nom du quartier (ex: "Downtown")

    @Column(name = "walk_score")
    private Integer walkScore; // Score de marche (0-100)

    @Column(name = "transit_score")
    private Integer transitScore; // Score de transport (0-100)

    @Column(name = "bike_score")
    private Integer bikeScore; // Score de vélo (0-100)

    @Size(max = 200)
    @Column(name = "building_name")
    private String buildingName; // Nom du bâtiment (ex: "7 Dekalb")

    @Column(name = "flooring", columnDefinition = "TEXT")
    private String flooring; // JSON array: ["Hardwood", "Carpet", etc.]

    @Column(name = "unit_features", columnDefinition = "TEXT")
    private String unitFeatures; // JSON array: ["Dishwasher", "Washer/Dryer", etc.]

    @Column(name = "building_amenities", columnDefinition = "TEXT")
    private String buildingAmenities; // JSON array: ["Fitness Center", "Roof Deck", etc.]

    @Column(name = "available_units", columnDefinition = "TEXT")
    private String availableUnits; // JSON array d'unités disponibles avec prix et dates

    @Column(name = "pet_policy", columnDefinition = "TEXT")
    private String petPolicy; // JSON pour la politique des animaux (dogsAllowed, catsAllowed, restrictions)

    @Column(name = "parking_policy")
    private String parkingPolicy; // "None", "Garage", "Street", etc.

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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public String getOgImage() {
        return ogImage;
    }

    public void setOgImage(String ogImage) {
        this.ogImage = ogImage;
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

    public java.time.LocalDate getDateOnMarket() {
        return dateOnMarket;
    }

    public void setDateOnMarket(java.time.LocalDate dateOnMarket) {
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

    // Getters and Setters for Zillow-inspired fields
    public Boolean getPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getSpecialOffer() {
        return specialOffer;
    }

    public void setSpecialOffer(String specialOffer) {
        this.specialOffer = specialOffer;
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Integer getWalkScore() {
        return walkScore;
    }

    public void setWalkScore(Integer walkScore) {
        this.walkScore = walkScore;
    }

    public Integer getTransitScore() {
        return transitScore;
    }

    public void setTransitScore(Integer transitScore) {
        this.transitScore = transitScore;
    }

    public Integer getBikeScore() {
        return bikeScore;
    }

    public void setBikeScore(Integer bikeScore) {
        this.bikeScore = bikeScore;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getFlooring() {
        return flooring;
    }

    public void setFlooring(String flooring) {
        this.flooring = flooring;
    }

    public String getUnitFeatures() {
        return unitFeatures;
    }

    public void setUnitFeatures(String unitFeatures) {
        this.unitFeatures = unitFeatures;
    }

    public String getBuildingAmenities() {
        return buildingAmenities;
    }

    public void setBuildingAmenities(String buildingAmenities) {
        this.buildingAmenities = buildingAmenities;
    }

    public String getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(String availableUnits) {
        this.availableUnits = availableUnits;
    }

    public String getPetPolicy() {
        return petPolicy;
    }

    public void setPetPolicy(String petPolicy) {
        this.petPolicy = petPolicy;
    }

    public String getParkingPolicy() {
        return parkingPolicy;
    }

    public void setParkingPolicy(String parkingPolicy) {
        this.parkingPolicy = parkingPolicy;
    }
}

