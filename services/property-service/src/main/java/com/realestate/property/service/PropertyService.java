package com.realestate.property.service;

import com.realestate.property.entity.Property;
import com.realestate.property.entity.PropertyAccess;
import com.realestate.property.entity.PropertyFeature;
import com.realestate.property.repository.PropertyAccessRepository;
import com.realestate.property.repository.PropertyFeatureRepository;
import com.realestate.property.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyAccessRepository propertyAccessRepository;
    private final PropertyFeatureRepository propertyFeatureRepository;

    public PropertyService(
            PropertyRepository propertyRepository,
            PropertyAccessRepository propertyAccessRepository,
            PropertyFeatureRepository propertyFeatureRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyAccessRepository = propertyAccessRepository;
        this.propertyFeatureRepository = propertyFeatureRepository;
    }

    @Transactional
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
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

        return propertyRepository.save(property);
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

