package com.realestate.property.mapper;

import com.realestate.property.dto.PropertyDTO;
import com.realestate.property.entity.Property;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper {

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
        return property;
    }
}

