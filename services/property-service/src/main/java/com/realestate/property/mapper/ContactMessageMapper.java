package com.realestate.property.mapper;

import com.realestate.property.dto.ContactMessageDTO;
import com.realestate.property.entity.ContactMessage;
import com.realestate.property.entity.Property;
import org.springframework.stereotype.Component;

@Component
public class ContactMessageMapper {

    public ContactMessageDTO toDTO(ContactMessage contactMessage) {
        if (contactMessage == null) {
            return null;
        }

        ContactMessageDTO dto = new ContactMessageDTO();
        dto.setId(contactMessage.getId());
        dto.setName(contactMessage.getName());
        dto.setEmail(contactMessage.getEmail());
        dto.setPhone(contactMessage.getPhone());
        dto.setSubject(contactMessage.getSubject());
        dto.setMessage(contactMessage.getMessage());
        dto.setStatus(contactMessage.getStatus());
        dto.setPropertyId(contactMessage.getPropertyId());
        dto.setOrganizationId(contactMessage.getOrganizationId());
        dto.setReadAt(contactMessage.getReadAt());
        dto.setReadBy(contactMessage.getReadBy());
        dto.setRepliedAt(contactMessage.getRepliedAt());
        dto.setRepliedBy(contactMessage.getRepliedBy());
        dto.setNotes(contactMessage.getNotes());
        dto.setActive(contactMessage.getActive());
        dto.setCreatedAt(contactMessage.getCreatedAt());
        dto.setUpdatedAt(contactMessage.getUpdatedAt());

        // Ajouter les informations de la propriété si disponible
        if (contactMessage.getProperty() != null) {
            Property property = contactMessage.getProperty();
            dto.setPropertyTitle(property.getTitle());
            dto.setPropertyReference(property.getReference());
        }

        return dto;
    }

    public ContactMessage toEntity(ContactMessageDTO dto) {
        if (dto == null) {
            return null;
        }

        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setId(dto.getId());
        contactMessage.setName(dto.getName());
        contactMessage.setEmail(dto.getEmail());
        contactMessage.setPhone(dto.getPhone());
        contactMessage.setSubject(dto.getSubject());
        contactMessage.setMessage(dto.getMessage());
        contactMessage.setStatus(dto.getStatus() != null ? dto.getStatus() : "NEW");
        contactMessage.setPropertyId(dto.getPropertyId());
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

