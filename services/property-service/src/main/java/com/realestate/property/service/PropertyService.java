package com.realestate.property.service;

import com.realestate.common.client.IdentityServiceClient;
import com.realestate.common.client.ResourceServiceClient;
import com.realestate.common.event.PropertyCreatedEvent;
import com.realestate.common.event.PropertyUpdatedEvent;
import com.realestate.property.entity.Property;
import com.realestate.property.entity.PropertyAccess;
import com.realestate.property.entity.PropertyFeature;
import com.realestate.property.repository.PropertyAccessRepository;
import com.realestate.property.repository.PropertyFeatureRepository;
import com.realestate.property.repository.PropertyRepository;
import com.realestate.property.specification.PropertySpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    private final PropertyRepository propertyRepository;
    private final PropertyAccessRepository propertyAccessRepository;
    private final PropertyFeatureRepository propertyFeatureRepository;
    private final PropertyEventProducer eventProducer;
    private final IdentityServiceClient identityServiceClient;
    private final ResourceServiceClient resourceServiceClient;

    public PropertyService(
            PropertyRepository propertyRepository,
            PropertyAccessRepository propertyAccessRepository,
            PropertyFeatureRepository propertyFeatureRepository,
            @Autowired(required = false) PropertyEventProducer eventProducer,
            @Autowired(required = false) IdentityServiceClient identityServiceClient,
            @Autowired(required = false) ResourceServiceClient resourceServiceClient) {
        this.propertyRepository = propertyRepository;
        this.propertyAccessRepository = propertyAccessRepository;
        this.propertyFeatureRepository = propertyFeatureRepository;
        this.eventProducer = eventProducer;
        this.identityServiceClient = identityServiceClient;
        this.resourceServiceClient = resourceServiceClient;
    }

    @Transactional
    public Property createProperty(Property property, String authToken) {
        // Optional: Validate permissions before creating (async validation)
        if (authToken != null && property.getCreatedBy() != null) {
            try {
                Boolean hasPermission = validateCreatePermission(
                        property.getCreatedBy(), 
                        property.getOrganizationId(), 
                        authToken
                ).block(); // Blocking call - in production, consider making this fully async
                
                if (hasPermission == null || !hasPermission) {
                    logger.warn("User {} does not have permission to create properties", property.getCreatedBy());
                    throw new RuntimeException("User does not have permission to create properties");
                }
            } catch (Exception e) {
                logger.error("Error validating permission for property creation", e);
                // In production, you might want to fail here or log and continue
                // For now, we'll log and continue
            }
        }

        Property saved = propertyRepository.save(property);
        
        // Publish event to Kafka (if Kafka is configured)
        if (eventProducer != null) {
            PropertyCreatedEvent event = new PropertyCreatedEvent(
                    saved.getOrganizationId(),
                    saved.getCreatedBy(),
                    saved.getId(),
                    saved.getReference(),
                    saved.getTitle(),
                    saved.getType(),
                    saved.getPrice(),
                    saved.getCity(),
                    saved.getCountry()
            );
            eventProducer.publishPropertyCreated(event);
        }
        
        return saved;
    }

    /**
     * Validate that user has permission to create properties
     */
    private Mono<Boolean> validateCreatePermission(Long userId, Long organizationId, String authToken) {
        if (identityServiceClient == null) {
            logger.warn("IdentityServiceClient not available, skipping permission validation");
            return Mono.just(true); // Allow if client is not available
        }
        return identityServiceClient.checkPermission(
                userId,
                "property:create",
                "Property",
                null,
                authToken
        );
    }

    @Transactional(readOnly = true)
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Property> getPropertyByReference(String reference) {
        return propertyRepository.findByReference(reference);
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesByOrganizationId(Long organizationId) {
        return propertyRepository.findActiveByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesByAssignedUserId(Long userId) {
        return propertyRepository.findActiveByAssignedUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesByTeamId(Long teamId) {
        return propertyRepository.findActiveByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesByStatus(String status) {
        return propertyRepository.findActiveByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesByType(String type) {
        return propertyRepository.findActiveByType(type);
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesByCity(String city) {
        return propertyRepository.findActiveByCity(city);
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return propertyRepository.findActiveByPriceRange(minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public List<Property> getPropertiesBySurfaceRange(BigDecimal minSurface, BigDecimal maxSurface) {
        return propertyRepository.findActiveBySurfaceRange(minSurface, maxSurface);
    }

    /**
     * Récupérer les propriétés avec filtres multiples en utilisant JPA Specifications
     */
    @Transactional(readOnly = true)
    public Page<Property> getPropertiesWithFilters(
            Long organizationId,
            Long assignedUserId,
            Long teamId,
            String status,
            String type,
            String city,
            String country,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minSurface,
            BigDecimal maxSurface,
            Integer bedrooms,
            Integer bathrooms,
            String search,
            Pageable pageable) {
        
        Specification<Property> spec = Specification.where(null);

        // Filtre par organisation
        if (organizationId != null) {
            spec = spec.and(PropertySpecification.hasOrganization(organizationId));
        }

        // Filtre par utilisateur assigné
        if (assignedUserId != null) {
            spec = spec.and(PropertySpecification.hasAssignedUser(assignedUserId));
        }

        // Filtre par team
        if (teamId != null) {
            spec = spec.and(PropertySpecification.hasTeam(teamId));
        }

        // Filtre par statut
        if (status != null && !status.isEmpty()) {
            spec = spec.and(PropertySpecification.hasStatus(status));
        }

        // Filtre par type
        if (type != null && !type.isEmpty()) {
            spec = spec.and(PropertySpecification.hasType(type));
        }

        // Filtre par ville
        if (city != null && !city.isEmpty()) {
            spec = spec.and(PropertySpecification.hasCity(city));
        }

        // Filtre par pays
        if (country != null && !country.isEmpty()) {
            spec = spec.and(PropertySpecification.hasCountry(country));
        }

        // Filtre par plage de prix
        if (minPrice != null || maxPrice != null) {
            spec = spec.and(PropertySpecification.hasPriceRange(minPrice, maxPrice));
        }

        // Filtre par plage de surface
        if (minSurface != null || maxSurface != null) {
            spec = spec.and(PropertySpecification.hasSurfaceRange(minSurface, maxSurface));
        }

        // Filtre par nombre de chambres
        if (bedrooms != null) {
            spec = spec.and(PropertySpecification.hasBedrooms(bedrooms));
        }

        // Filtre par nombre de salles de bain
        if (bathrooms != null) {
            spec = spec.and(PropertySpecification.hasBathrooms(bathrooms));
        }

        // Recherche textuelle
        if (search != null && !search.isEmpty()) {
            spec = spec.and(PropertySpecification.searchByText(search));
        }

        // Par défaut, ne retourner que les propriétés actives
        spec = spec.and(PropertySpecification.isActive(true));

        return propertyRepository.findAll(spec, pageable);
    }

    /**
     * Récupérer toutes les propriétés avec filtres (sans pagination)
     */
    @Transactional(readOnly = true)
    public List<Property> getAllPropertiesWithFilters(
            Long organizationId,
            Long assignedUserId,
            Long teamId,
            String status,
            String type,
            String city,
            String country,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minSurface,
            BigDecimal maxSurface,
            Integer bedrooms,
            Integer bathrooms,
            String search) {
        
        Specification<Property> spec = Specification.where(null);

        if (organizationId != null) {
            spec = spec.and(PropertySpecification.hasOrganization(organizationId));
        }
        if (assignedUserId != null) {
            spec = spec.and(PropertySpecification.hasAssignedUser(assignedUserId));
        }
        if (teamId != null) {
            spec = spec.and(PropertySpecification.hasTeam(teamId));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and(PropertySpecification.hasStatus(status));
        }
        if (type != null && !type.isEmpty()) {
            spec = spec.and(PropertySpecification.hasType(type));
        }
        if (city != null && !city.isEmpty()) {
            spec = spec.and(PropertySpecification.hasCity(city));
        }
        if (country != null && !country.isEmpty()) {
            spec = spec.and(PropertySpecification.hasCountry(country));
        }
        if (minPrice != null || maxPrice != null) {
            spec = spec.and(PropertySpecification.hasPriceRange(minPrice, maxPrice));
        }
        if (minSurface != null || maxSurface != null) {
            spec = spec.and(PropertySpecification.hasSurfaceRange(minSurface, maxSurface));
        }
        if (bedrooms != null) {
            spec = spec.and(PropertySpecification.hasBedrooms(bedrooms));
        }
        if (bathrooms != null) {
            spec = spec.and(PropertySpecification.hasBathrooms(bathrooms));
        }
        if (search != null && !search.isEmpty()) {
            spec = spec.and(PropertySpecification.searchByText(search));
        }

        spec = spec.and(PropertySpecification.isActive(true));

        return propertyRepository.findAll(spec);
    }

    @Transactional
    public Property updateProperty(Long id, Property propertyDetails) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        if (propertyDetails.getTitle() != null) {
            property.setTitle(propertyDetails.getTitle());
        }
        if (propertyDetails.getDescription() != null) {
            property.setDescription(propertyDetails.getDescription());
        }
        if (propertyDetails.getType() != null) {
            property.setType(propertyDetails.getType());
        }
        if (propertyDetails.getStatus() != null) {
            property.setStatus(propertyDetails.getStatus());
        }
        if (propertyDetails.getPrice() != null) {
            property.setPrice(propertyDetails.getPrice());
        }
        if (propertyDetails.getCurrency() != null) {
            property.setCurrency(propertyDetails.getCurrency());
        }
        if (propertyDetails.getSurface() != null) {
            property.setSurface(propertyDetails.getSurface());
        }
        if (propertyDetails.getRooms() != null) {
            property.setRooms(propertyDetails.getRooms());
        }
        if (propertyDetails.getBedrooms() != null) {
            property.setBedrooms(propertyDetails.getBedrooms());
        }
        if (propertyDetails.getBathrooms() != null) {
            property.setBathrooms(propertyDetails.getBathrooms());
        }
        if (propertyDetails.getAddress() != null) {
            property.setAddress(propertyDetails.getAddress());
        }
        if (propertyDetails.getCity() != null) {
            property.setCity(propertyDetails.getCity());
        }
        if (propertyDetails.getPostalCode() != null) {
            property.setPostalCode(propertyDetails.getPostalCode());
        }
        if (propertyDetails.getCountry() != null) {
            property.setCountry(propertyDetails.getCountry());
        }
        if (propertyDetails.getLatitude() != null) {
            property.setLatitude(propertyDetails.getLatitude());
        }
        if (propertyDetails.getLongitude() != null) {
            property.setLongitude(propertyDetails.getLongitude());
        }
        if (propertyDetails.getAssignedUserId() != null) {
            property.setAssignedUserId(propertyDetails.getAssignedUserId());
        }
        if (propertyDetails.getTeamId() != null) {
            property.setTeamId(propertyDetails.getTeamId());
        }
        if (propertyDetails.getActive() != null) {
            property.setActive(propertyDetails.getActive());
        }
        if (propertyDetails.getFeatures() != null) {
            property.setFeatures(propertyDetails.getFeatures());
        }
        
        // Update new detailed fields
        if (propertyDetails.getFullBathrooms() != null) {
            property.setFullBathrooms(propertyDetails.getFullBathrooms());
        }
        if (propertyDetails.getAppliancesIncluded() != null) {
            property.setAppliancesIncluded(propertyDetails.getAppliancesIncluded());
        }
        if (propertyDetails.getLaundryLocation() != null) {
            property.setLaundryLocation(propertyDetails.getLaundryLocation());
        }
        if (propertyDetails.getTotalStructureArea() != null) {
            property.setTotalStructureArea(propertyDetails.getTotalStructureArea());
        }
        if (propertyDetails.getTotalInteriorLivableArea() != null) {
            property.setTotalInteriorLivableArea(propertyDetails.getTotalInteriorLivableArea());
        }
        if (propertyDetails.getVirtualTourUrl() != null) {
            property.setVirtualTourUrl(propertyDetails.getVirtualTourUrl());
        }
        if (propertyDetails.getParkingFeatures() != null) {
            property.setParkingFeatures(propertyDetails.getParkingFeatures());
        }
        if (propertyDetails.getHasGarage() != null) {
            property.setHasGarage(propertyDetails.getHasGarage());
        }
        if (propertyDetails.getAccessibilityFeatures() != null) {
            property.setAccessibilityFeatures(propertyDetails.getAccessibilityFeatures());
        }
        if (propertyDetails.getPatioPorch() != null) {
            property.setPatioPorch(propertyDetails.getPatioPorch());
        }
        if (propertyDetails.getExteriorFeatures() != null) {
            property.setExteriorFeatures(propertyDetails.getExteriorFeatures());
        }
        if (propertyDetails.getSpecialConditions() != null) {
            property.setSpecialConditions(propertyDetails.getSpecialConditions());
        }
        if (propertyDetails.getHomeType() != null) {
            property.setHomeType(propertyDetails.getHomeType());
        }
        if (propertyDetails.getPropertySubtype() != null) {
            property.setPropertySubtype(propertyDetails.getPropertySubtype());
        }
        if (propertyDetails.getCondition() != null) {
            property.setCondition(propertyDetails.getCondition());
        }
        if (propertyDetails.getYearBuilt() != null) {
            property.setYearBuilt(propertyDetails.getYearBuilt());
        }
        if (propertyDetails.getSubdivision() != null) {
            property.setSubdivision(propertyDetails.getSubdivision());
        }
        if (propertyDetails.getHasHOA() != null) {
            property.setHasHOA(propertyDetails.getHasHOA());
        }
        if (propertyDetails.getHoaAmenities() != null) {
            property.setHoaAmenities(propertyDetails.getHoaAmenities());
        }
        if (propertyDetails.getHoaServices() != null) {
            property.setHoaServices(propertyDetails.getHoaServices());
        }
        if (propertyDetails.getHoaFee() != null) {
            property.setHoaFee(propertyDetails.getHoaFee());
        }
        if (propertyDetails.getHoaFeeFrequency() != null) {
            property.setHoaFeeFrequency(propertyDetails.getHoaFeeFrequency());
        }
        if (propertyDetails.getRegion() != null) {
            property.setRegion(propertyDetails.getRegion());
        }
        if (propertyDetails.getPricePerSquareFoot() != null) {
            property.setPricePerSquareFoot(propertyDetails.getPricePerSquareFoot());
        }
        if (propertyDetails.getDateOnMarket() != null) {
            property.setDateOnMarket(propertyDetails.getDateOnMarket());
        }

        Property updated = propertyRepository.save(property);
        
        // Publish event to Kafka (if Kafka is configured)
        if (eventProducer != null) {
            PropertyUpdatedEvent event = new PropertyUpdatedEvent(
                    updated.getOrganizationId(),
                    updated.getCreatedBy(),
                    updated.getId(),
                    updated.getReference(),
                    updated.getTitle(),
                    updated.getType(),
                    updated.getPrice(),
                    updated.getStatus(),
                    updated.getCity(),
                    updated.getCountry()
            );
            eventProducer.publishPropertyUpdated(event);
        }
        
        return updated;
    }

    @Transactional
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found with id: " + id);
        }
        propertyRepository.deleteById(id);
    }

    @Transactional
    public PropertyAccess sharePropertyWithOrganization(
            Long propertyId,
            Long organizationId,
            Long userId,
            Boolean canRead,
            Boolean canWrite,
            Boolean canDelete,
            Long grantedBy) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

        PropertyAccess access = propertyAccessRepository
                .findByPropertyIdAndUserIdAndOrganizationId(propertyId, userId, organizationId)
                .orElse(new PropertyAccess(property, organizationId));

        access.setUserId(userId);
        access.setCanRead(canRead != null ? canRead : true);
        access.setCanWrite(canWrite != null ? canWrite : false);
        access.setCanDelete(canDelete != null ? canDelete : false);
        access.setGrantedBy(grantedBy);
        access.setActive(true);

        return propertyAccessRepository.save(access);
    }

    @Transactional
    public PropertyFeature addFeatureToProperty(Long propertyId, PropertyFeature feature) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

        PropertyFeature existingFeature = propertyFeatureRepository
                .findByPropertyIdAndKey(propertyId, feature.getKey())
                .orElse(null);

        if (existingFeature != null) {
            existingFeature.setValue(feature.getValue());
            existingFeature.setType(feature.getType());
            existingFeature.setActive(true);
            return propertyFeatureRepository.save(existingFeature);
        } else {
            feature.setProperty(property);
            return propertyFeatureRepository.save(feature);
        }
    }

    @Transactional
    public void removeFeatureFromProperty(Long propertyId, String featureKey) {
        PropertyFeature feature = propertyFeatureRepository
                .findByPropertyIdAndKey(propertyId, featureKey)
                .orElseThrow(() -> new RuntimeException("Feature not found: " + featureKey));
        propertyFeatureRepository.delete(feature);
    }

    /**
     * Get all active features for a property
     */
    @Transactional(readOnly = true)
    public List<PropertyFeature> getPropertyFeatures(Long propertyId) {
        return propertyFeatureRepository.findActiveByPropertyId(propertyId);
    }

    /**
     * Add or update multiple features for a property
     * Useful for managing JSON arrays as individual PropertyFeatures
     */
    @Transactional
    public List<PropertyFeature> addFeaturesToProperty(Long propertyId, List<PropertyFeature> features) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

        for (PropertyFeature feature : features) {
            PropertyFeature existingFeature = propertyFeatureRepository
                    .findByPropertyIdAndKey(propertyId, feature.getKey())
                    .orElse(null);

            if (existingFeature != null) {
                existingFeature.setValue(feature.getValue());
                existingFeature.setType(feature.getType());
                existingFeature.setActive(true);
                propertyFeatureRepository.save(existingFeature);
            } else {
                feature.setProperty(property);
                propertyFeatureRepository.save(feature);
            }
        }

        return propertyFeatureRepository.findActiveByPropertyId(propertyId);
    }

    /**
     * Synchronize a JSON array as PropertyFeatures
     * Removes existing features with the same key and adds new ones
     */
    @Transactional
    public List<PropertyFeature> syncFeaturesFromJsonArray(
            Long propertyId, 
            String featureKey, 
            List<String> values) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

        // Remove existing features with this key
        List<PropertyFeature> existingFeatures = propertyFeatureRepository
                .findByPropertyId(propertyId)
                .stream()
                .filter(pf -> pf.getKey().equals(featureKey))
                .collect(Collectors.toList());
        
        propertyFeatureRepository.deleteAll(existingFeatures);

        // Add new features
        for (String value : values) {
            PropertyFeature feature = new PropertyFeature(property, featureKey, value);
            feature.setType("STRING");
            propertyFeatureRepository.save(feature);
        }

        return propertyFeatureRepository.findActiveByPropertyId(propertyId);
    }

    /**
     * Convert PropertyFeatures to JSON array string
     */
    public String convertFeaturesToJsonArray(List<PropertyFeature> features) {
        if (features == null || features.isEmpty()) {
            return "[]";
        }
        List<String> values = features.stream()
                .map(PropertyFeature::getValue)
                .filter(v -> v != null && !v.isEmpty())
                .collect(Collectors.toList());
        return "[" + values.stream()
                .map(v -> "\"" + v.replace("\"", "\\\"") + "\"")
                .collect(java.util.stream.Collectors.joining(", ")) + "]";
    }
}

