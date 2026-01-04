package com.realestate.identity.mapper;

import com.realestate.identity.dto.OrganizationContactMessageDTO;
import com.realestate.identity.entity.Organization;
import com.realestate.identity.entity.OrganizationContactMessage;
import org.springframework.stereotype.Component;

@Component
public class OrganizationContactMessageMapper {

    public OrganizationContactMessageDTO toDTO(OrganizationContactMessage contactMessage) {
        if (contactMessage == null) {
            return null;
        }

        OrganizationContactMessageDTO dto = new OrganizationContactMessageDTO();
        dto.setId(contactMessage.getId());
        dto.setName(contactMessage.getName());
        dto.setEmail(contactMessage.getEmail());
        dto.setPhone(contactMessage.getPhone());
        dto.setSubject(contactMessage.getSubject());
        dto.setMessage(contactMessage.getMessage());
        dto.setStatus(contactMessage.getStatus());
        dto.setOrganizationId(contactMessage.getOrganizationId());
        dto.setReadAt(contactMessage.getReadAt());
        dto.setReadBy(contactMessage.getReadBy());
        dto.setRepliedAt(contactMessage.getRepliedAt());
        dto.setRepliedBy(contactMessage.getRepliedBy());
        dto.setNotes(contactMessage.getNotes());
        dto.setActive(contactMessage.getActive());
        dto.setCreatedAt(contactMessage.getCreatedAt());
        dto.setUpdatedAt(contactMessage.getUpdatedAt());

        // Ajouter les informations de l'organisation si disponible
        if (contactMessage.getOrganization() != null) {
            Organization organization = contactMessage.getOrganization();
            dto.setOrganizationName(organization.getName());
        }

        return dto;
    }

    public OrganizationContactMessage toEntity(OrganizationContactMessageDTO dto) {
        if (dto == null) {
            return null;
        }

        OrganizationContactMessage contactMessage = new OrganizationContactMessage();
        contactMessage.setId(dto.getId());
        contactMessage.setName(dto.getName());
        contactMessage.setEmail(dto.getEmail());
        contactMessage.setPhone(dto.getPhone());
        contactMessage.setSubject(dto.getSubject());
        contactMessage.setMessage(dto.getMessage());
        contactMessage.setStatus(dto.getStatus() != null ? dto.getStatus() : "NEW");
        contactMessage.setOrganizationId(dto.getOrganizationId());
        contactMessage.setReadAt(dto.getReadAt());
        contactMessage.setReadBy(dto.getReadBy());
        contactMessage.setRepliedAt(dto.getRepliedAt());
        contactMessage.setRepliedBy(dto.getRepliedBy());
        contactMessage.setNotes(dto.getNotes());
        contactMessage.setActive(dto.getActive() != null ? dto.getActive() : true);

        return contactMessage;
    }
}

