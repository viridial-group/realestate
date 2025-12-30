package com.realestate.resource.mapper;

import com.realestate.resource.dto.ResourceDTO;
import com.realestate.resource.entity.Resource;
import com.realestate.resource.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ResourceMapper {

    public ResourceDTO toDTO(Resource resource) {
        if (resource == null) {
            return null;
        }
        ResourceDTO dto = new ResourceDTO();
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        dto.setDescription(resource.getDescription());
        dto.setOrganizationId(resource.getOrganizationId());
        dto.setCreatedBy(resource.getCreatedBy());
        dto.setActive(resource.getActive());
        dto.setShared(resource.getShared());
        dto.setMetadata(resource.getMetadata());
        
        // Mapper le domaine (évite lazy loading)
        if (resource.getDomain() != null) {
            dto.setDomainId(resource.getDomain().getId());
            dto.setDomainName(resource.getDomain().getName());
        }
        
        // Mapper les tags en noms seulement (évite lazy loading)
        if (resource.getTags() != null) {
            dto.setTagNames(resource.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet()));
        }
        
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        return dto;
    }

    public Resource toEntity(ResourceDTO dto) {
        if (dto == null) {
            return null;
        }
        Resource resource = new Resource();
        resource.setId(dto.getId());
        resource.setName(dto.getName());
        resource.setDescription(dto.getDescription());
        resource.setOrganizationId(dto.getOrganizationId());
        resource.setCreatedBy(dto.getCreatedBy());
        resource.setActive(dto.getActive());
        resource.setShared(dto.getShared());
        resource.setMetadata(dto.getMetadata());
        // Note: domain et tags doivent être gérés séparément
        return resource;
    }
}

