package com.realestate.emailing.mapper;

import com.realestate.emailing.dto.EmailDTO;
import com.realestate.emailing.entity.Email;
import org.springframework.stereotype.Component;

@Component
public class EmailMapper {

    public EmailDTO toDTO(Email email) {
        if (email == null) {
            return null;
        }
        EmailDTO dto = new EmailDTO();
        dto.setId(email.getId());
        dto.setRecipientEmail(email.getRecipientEmail());
        dto.setRecipientId(email.getRecipientId());
        dto.setSubject(email.getSubject());
        dto.setBody(email.getBody());
        dto.setStatus(email.getStatus());
        dto.setOrganizationId(email.getOrganizationId());
        dto.setSenderEmail(email.getSenderEmail());
        dto.setSentAt(email.getSentAt());
        dto.setFailedAt(email.getFailedAt());
        dto.setErrorMessage(email.getErrorMessage());
        dto.setRetryCount(email.getRetryCount());
        dto.setVariables(email.getVariables());
        dto.setActive(email.getActive());
        dto.setCreatedAt(email.getCreatedAt());
        dto.setUpdatedAt(email.getUpdatedAt());
        
        // Mapper le template (évite lazy loading)
        if (email.getTemplate() != null) {
            dto.setTemplateId(email.getTemplate().getId());
            dto.setTemplateName(email.getTemplate().getName());
        }
        
        return dto;
    }

    public Email toEntity(EmailDTO dto) {
        if (dto == null) {
            return null;
        }
        Email email = new Email();
        email.setId(dto.getId());
        email.setRecipientEmail(dto.getRecipientEmail());
        email.setRecipientId(dto.getRecipientId());
        email.setSubject(dto.getSubject());
        email.setBody(dto.getBody());
        email.setStatus(dto.getStatus());
        email.setOrganizationId(dto.getOrganizationId());
        email.setSenderEmail(dto.getSenderEmail());
        email.setSentAt(dto.getSentAt());
        email.setFailedAt(dto.getFailedAt());
        email.setErrorMessage(dto.getErrorMessage());
        email.setRetryCount(dto.getRetryCount());
        email.setVariables(dto.getVariables());
        email.setActive(dto.getActive());
        // Note: template doit être géré séparément
        return email;
    }
}

