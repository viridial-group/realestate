package com.realestate.resource.mapper;

import com.realestate.resource.dto.DomainDTO;
import com.realestate.resource.entity.Domain;
import org.springframework.stereotype.Component;

@Component
public class DomainMapper {

    public DomainDTO toDTO(Domain domain) {
        if (domain == null) {
            return null;
        }
        DomainDTO dto = new DomainDTO();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setDescription(domain.getDescription());
        dto.setActive(domain.getActive());
        dto.setCreatedAt(domain.getCreatedAt());
        dto.setUpdatedAt(domain.getUpdatedAt());
        return dto;
    }

    public Domain toEntity(DomainDTO dto) {
        if (dto == null) {
            return null;
        }
        Domain domain = new Domain();
        domain.setId(dto.getId());
        domain.setName(dto.getName());
        domain.setDescription(dto.getDescription());
        domain.setActive(dto.getActive());
        return domain;
    }
}

