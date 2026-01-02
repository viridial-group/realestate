package com.realestate.property.mapper;

import com.realestate.common.client.IdentityServiceClient;
import com.realestate.common.client.dto.OrganizationInfoDTO;
import com.realestate.common.client.dto.UserInfoDTO;
import com.realestate.property.dto.PropertyDTO;
import com.realestate.property.entity.Property;
import com.realestate.property.entity.PropertyFeature;
import com.realestate.property.repository.PropertyFeatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PropertyMapper {

    private static final Logger logger = LoggerFactory.getLogger(PropertyMapper.class);

    @Autowired(required = false)
    private PropertyFeatureRepository propertyFeatureRepository;

    @Autowired(required = false)
    private IdentityServiceClient identityServiceClient;

    public PropertyDTO toDTO(Property property) {
        if (property == null) {
            return null;
        }
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setReference(property.getReference());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setType(property.getType());
        dto.setStatus(property.getStatus());
        dto.setPrice(property.getPrice());
        dto.setCurrency(property.getCurrency());
        dto.setSurface(property.getSurface());
        dto.setRooms(property.getRooms());
        dto.setBedrooms(property.getBedrooms());
        dto.setBathrooms(property.getBathrooms());
        dto.setAddress(property.getAddress());
        dto.setCity(property.getCity());
        dto.setPostalCode(property.getPostalCode());
        dto.setCountry(property.getCountry());
        dto.setLatitude(property.getLatitude());
        dto.setLongitude(property.getLongitude());
        dto.setOrganizationId(property.getOrganizationId());
        dto.setAssignedUserId(property.getAssignedUserId());
        dto.setMetadata(property.getFeatures()); // features est stocké comme JSON
        
        // Enrich with organization name and assigned user name (async, but we'll do it synchronously for now)
        if (property.getOrganizationId() != null && identityServiceClient != null) {
            try {
                Optional<OrganizationInfoDTO> orgInfo = identityServiceClient
                        .getOrganizationById(property.getOrganizationId(), null)
                        .block();
                if (orgInfo.isPresent()) {
                    dto.setOrganizationName(orgInfo.get().getName());
                }
            } catch (Exception e) {
                logger.warn("Failed to fetch organization name for ID {}: {}", property.getOrganizationId(), e.getMessage());
            }
        }
        
        if (property.getAssignedUserId() != null && identityServiceClient != null) {
            try {
                Optional<UserInfoDTO> userInfo = identityServiceClient
                        .getUserById(property.getAssignedUserId(), null)
                        .block();
                if (userInfo.isPresent()) {
                    UserInfoDTO user = userInfo.get();
                    String fullName = (user.getFirstName() != null ? user.getFirstName() : "") +
                            (user.getLastName() != null ? " " + user.getLastName() : "").trim();
                    dto.setAssignedUserName(fullName.isEmpty() ? user.getEmail() : fullName);
                }
            } catch (Exception e) {
                logger.warn("Failed to fetch user name for ID {}: {}", property.getAssignedUserId(), e.getMessage());
            }
        }
        
        // New detailed fields
        dto.setFullBathrooms(property.getFullBathrooms());
        dto.setAppliancesIncluded(property.getAppliancesIncluded());
        dto.setLaundryLocation(property.getLaundryLocation());
        dto.setTotalStructureArea(property.getTotalStructureArea());
        dto.setTotalInteriorLivableArea(property.getTotalInteriorLivableArea());
        dto.setVirtualTourUrl(property.getVirtualTourUrl());
        dto.setParkingFeatures(property.getParkingFeatures());
        dto.setHasGarage(property.getHasGarage());
        dto.setAccessibilityFeatures(property.getAccessibilityFeatures());
        dto.setPatioPorch(property.getPatioPorch());
        dto.setExteriorFeatures(property.getExteriorFeatures());
        dto.setSpecialConditions(property.getSpecialConditions());
        dto.setHomeType(property.getHomeType());
        dto.setPropertySubtype(property.getPropertySubtype());
        dto.setCondition(property.getCondition());
        dto.setYearBuilt(property.getYearBuilt());
        dto.setSubdivision(property.getSubdivision());
        dto.setHasHOA(property.getHasHOA());
        dto.setHoaAmenities(property.getHoaAmenities());
        dto.setHoaServices(property.getHoaServices());
        dto.setHoaFee(property.getHoaFee());
        dto.setHoaFeeFrequency(property.getHoaFeeFrequency());
        dto.setRegion(property.getRegion());
        dto.setPricePerSquareFoot(property.getPricePerSquareFoot());
        dto.setDateOnMarket(property.getDateOnMarket());
        
        // Load PropertyFeatures if repository is available
        if (propertyFeatureRepository != null && property.getId() != null) {
            List<PropertyFeature> features = propertyFeatureRepository.findActiveByPropertyId(property.getId());
            // Convert PropertyFeatures to JSON arrays for fields that might be stored as features
            // This allows backward compatibility - if fields are in Property, use them; otherwise use PropertyFeatures
            if (dto.getAppliancesIncluded() == null || dto.getAppliancesIncluded().isEmpty()) {
                String appliancesJson = convertFeaturesToJsonArray(features, "appliance");
                if (appliancesJson != null && !appliancesJson.equals("[]")) {
                    dto.setAppliancesIncluded(appliancesJson);
                }
            }
            if (dto.getParkingFeatures() == null || dto.getParkingFeatures().isEmpty()) {
                String parkingJson = convertFeaturesToJsonArray(features, "parking_feature");
                if (parkingJson != null && !parkingJson.equals("[]")) {
                    dto.setParkingFeatures(parkingJson);
                }
            }
            if (dto.getAccessibilityFeatures() == null || dto.getAccessibilityFeatures().isEmpty()) {
                String accessibilityJson = convertFeaturesToJsonArray(features, "accessibility_feature");
                if (accessibilityJson != null && !accessibilityJson.equals("[]")) {
                    dto.setAccessibilityFeatures(accessibilityJson);
                }
            }
            if (dto.getExteriorFeatures() == null || dto.getExteriorFeatures().isEmpty()) {
                String exteriorJson = convertFeaturesToJsonArray(features, "exterior_feature");
                if (exteriorJson != null && !exteriorJson.equals("[]")) {
                    dto.setExteriorFeatures(exteriorJson);
                }
            }
            if (dto.getHoaAmenities() == null || dto.getHoaAmenities().isEmpty()) {
                String hoaAmenitiesJson = convertFeaturesToJsonArray(features, "hoa_amenity");
                if (hoaAmenitiesJson != null && !hoaAmenitiesJson.equals("[]")) {
                    dto.setHoaAmenities(hoaAmenitiesJson);
                }
            }
            if (dto.getHoaServices() == null || dto.getHoaServices().isEmpty()) {
                String hoaServicesJson = convertFeaturesToJsonArray(features, "hoa_service");
                if (hoaServicesJson != null && !hoaServicesJson.equals("[]")) {
                    dto.setHoaServices(hoaServicesJson);
                }
            }
            // Single value features
            if (dto.getPatioPorch() == null || dto.getPatioPorch().isEmpty()) {
                String patioPorch = getFeatureValue(features, "patio_porch");
                if (patioPorch != null) {
                    dto.setPatioPorch(patioPorch);
                }
            }
            if (dto.getSpecialConditions() == null || dto.getSpecialConditions().isEmpty()) {
                String specialConditions = getFeatureValue(features, "special_condition");
                if (specialConditions != null) {
                    dto.setSpecialConditions(specialConditions);
                }
            }
        }
        
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());
        return dto;
    }

    public Property toEntity(PropertyDTO dto) {
        if (dto == null) {
            return null;
        }
        Property property = new Property();
        property.setId(dto.getId());
        property.setReference(dto.getReference());
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setType(dto.getType());
        property.setStatus(dto.getStatus());
        property.setPrice(dto.getPrice());
        property.setCurrency(dto.getCurrency());
        property.setSurface(dto.getSurface());
        property.setRooms(dto.getRooms());
        property.setBedrooms(dto.getBedrooms());
        property.setBathrooms(dto.getBathrooms());
        property.setAddress(dto.getAddress());
        property.setCity(dto.getCity());
        property.setPostalCode(dto.getPostalCode());
        property.setCountry(dto.getCountry());
        property.setLatitude(dto.getLatitude());
        property.setLongitude(dto.getLongitude());
        property.setOrganizationId(dto.getOrganizationId());
        property.setAssignedUserId(dto.getAssignedUserId());
        property.setFeatures(dto.getMetadata()); // metadata DTO → features entity
        
        // New detailed fields
        property.setFullBathrooms(dto.getFullBathrooms());
        property.setAppliancesIncluded(dto.getAppliancesIncluded());
        property.setLaundryLocation(dto.getLaundryLocation());
        property.setTotalStructureArea(dto.getTotalStructureArea());
        property.setTotalInteriorLivableArea(dto.getTotalInteriorLivableArea());
        property.setVirtualTourUrl(dto.getVirtualTourUrl());
        property.setParkingFeatures(dto.getParkingFeatures());
        property.setHasGarage(dto.getHasGarage());
        property.setAccessibilityFeatures(dto.getAccessibilityFeatures());
        property.setPatioPorch(dto.getPatioPorch());
        property.setExteriorFeatures(dto.getExteriorFeatures());
        property.setSpecialConditions(dto.getSpecialConditions());
        property.setHomeType(dto.getHomeType());
        property.setPropertySubtype(dto.getPropertySubtype());
        property.setCondition(dto.getCondition());
        property.setYearBuilt(dto.getYearBuilt());
        property.setSubdivision(dto.getSubdivision());
        property.setHasHOA(dto.getHasHOA());
        property.setHoaAmenities(dto.getHoaAmenities());
        property.setHoaServices(dto.getHoaServices());
        property.setHoaFee(dto.getHoaFee());
        property.setHoaFeeFrequency(dto.getHoaFeeFrequency());
        property.setRegion(dto.getRegion());
        property.setPricePerSquareFoot(dto.getPricePerSquareFoot());
        property.setDateOnMarket(dto.getDateOnMarket());
        
        return property;
    }

    /**
     * Convert PropertyFeatures with a specific key to JSON array string
     */
    private String convertFeaturesToJsonArray(List<PropertyFeature> features, String key) {
        if (features == null || features.isEmpty()) {
            return null;
        }
        List<String> values = features.stream()
                .filter(f -> key.equals(f.getKey()))
                .map(PropertyFeature::getValue)
                .filter(v -> v != null && !v.isEmpty())
                .collect(Collectors.toList());
        
        if (values.isEmpty()) {
            return null;
        }
        
        return "[" + values.stream()
                .map(v -> "\"" + v.replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(", ")) + "]";
    }

    /**
     * Get single feature value by key
     */
    private String getFeatureValue(List<PropertyFeature> features, String key) {
        if (features == null || features.isEmpty()) {
            return null;
        }
        return features.stream()
                .filter(f -> key.equals(f.getKey()))
                .map(PropertyFeature::getValue)
                .findFirst()
                .orElse(null);
    }
}

