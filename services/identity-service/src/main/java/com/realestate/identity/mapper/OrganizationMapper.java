package com.realestate.identity.mapper;

import com.realestate.identity.dto.OrganizationDTO;
import com.realestate.identity.entity.Organization;
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
        
        // Paramètres d'organisation
        dto.setLogoUrl(organization.getLogoUrl());
        dto.setAddress(organization.getAddress());
        dto.setCity(organization.getCity());
        dto.setPostalCode(organization.getPostalCode());
        dto.setCountry(organization.getCountry());
        dto.setPhone(organization.getPhone());
        dto.setEmail(organization.getEmail());
        dto.setCustomDomains(organization.getCustomDomains());
        dto.setQuotas(organization.getQuotas());
        
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
        
        // Paramètres d'organisation
        organization.setLogoUrl(dto.getLogoUrl());
        organization.setAddress(dto.getAddress());
        organization.setCity(dto.getCity());
        organization.setPostalCode(dto.getPostalCode());
        organization.setCountry(dto.getCountry());
        organization.setPhone(dto.getPhone());
        organization.setEmail(dto.getEmail());
        organization.setCustomDomains(dto.getCustomDomains());
        organization.setQuotas(dto.getQuotas());
        
        // Note: parent doit être géré séparément
        return organization;
    }
}

