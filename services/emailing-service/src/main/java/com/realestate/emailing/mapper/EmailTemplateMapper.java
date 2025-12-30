package com.realestate.emailing.mapper;

import com.realestate.emailing.dto.EmailTemplateDTO;
import com.realestate.emailing.entity.EmailTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateMapper {

    public EmailTemplateDTO toDTO(EmailTemplate template) {
        if (template == null) {
            return null;
        }
        EmailTemplateDTO dto = new EmailTemplateDTO();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setDescription(template.getDescription());
        dto.setSubject(template.getSubject());
        dto.setBody(template.getBody());
        dto.setOrganizationId(template.getOrganizationId());
        dto.setVariables(template.getAvailableVariables());
        dto.setActive(template.getActive());
        dto.setIsDefault(template.getIsDefault());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setUpdatedAt(template.getUpdatedAt());
        return dto;
    }

    public EmailTemplate toEntity(EmailTemplateDTO dto) {
        if (dto == null) {
            return null;
        }
        EmailTemplate template = new EmailTemplate();
        template.setId(dto.getId());
        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        template.setSubject(dto.getSubject());
        template.setBody(dto.getBody());
        template.setOrganizationId(dto.getOrganizationId());
        template.setAvailableVariables(dto.getVariables());
        template.setActive(dto.getActive());
        template.setIsDefault(dto.getIsDefault());
        return template;
    }
}

