package com.realestate.organization.mapper;

import com.realestate.organization.dto.OrganizationDTO;
import com.realestate.organization.entity.Organization;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

    public OrganizationDTO toDTO(Organization organization) {
        if (organization == null) {
            return null;
        }
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(organization.getId());
        dto.setName(organization.getName());
        dto.setDescription(organization.getDescription());
        dto.setDomain(organization.getDomain());
        dto.setActive(organization.getActive());
        
        // Mapper seulement l'ID du parent (évite lazy loading)
        if (organization.getParent() != null) {
            dto.setParentId(organization.getParent().getId());
        }
        
        dto.setCreatedAt(organization.getCreatedAt());
        dto.setUpdatedAt(organization.getUpdatedAt());
        return dto;
    }

    public Organization toEntity(OrganizationDTO dto) {
        if (dto == null) {
            return null;
        }
        Organization organization = new Organization();
        organization.setId(dto.getId());
        organization.setName(dto.getName());
        organization.setDescription(dto.getDescription());
        organization.setDomain(dto.getDomain());
        organization.setActive(dto.getActive());
        // Note: parent doit être géré séparément
        return organization;
    }
}

