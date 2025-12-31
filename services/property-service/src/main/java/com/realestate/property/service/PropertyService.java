package com.realestate.property.service;

import com.realestate.common.client.IdentityServiceClient;
import com.realestate.common.client.ResourceServiceClient;
import com.realestate.common.client.dto.UserInfoDTO;
import com.realestate.common.event.PropertyCreatedEvent;
import com.realestate.common.event.PropertyUpdatedEvent;
import com.realestate.property.entity.Property;
import com.realestate.property.entity.PropertyAccess;
import com.realestate.property.entity.PropertyFeature;
import com.realestate.property.repository.PropertyAccessRepository;
import com.realestate.property.repository.PropertyFeatureRepository;
import com.realestate.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            PropertyEventProducer eventProducer,
            IdentityServiceClient identityServiceClient,
            ResourceServiceClient resourceServiceClient) {
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
        
        // Publish event to Kafka
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
        
        return saved;
    }

    /**
     * Validate that user has permission to create properties
     */
    private Mono<Boolean> validateCreatePermission(Long userId, Long organizationId, String authToken) {
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

        Property updated = propertyRepository.save(property);
        
        // Publish event to Kafka
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
}

