package com.realestate.emailing.service;

import com.realestate.emailing.entity.EmailTemplate;
import com.realestate.emailing.repository.EmailTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmailTemplateService {

    private final EmailTemplateRepository templateRepository;

    public EmailTemplateService(EmailTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Transactional
    public EmailTemplate createTemplate(EmailTemplate template) {
        return templateRepository.save(template);
    }

    @Transactional(readOnly = true)
    public Optional<EmailTemplate> getTemplateById(Long id) {
        return templateRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<EmailTemplate> getTemplatesByType(String type) {
        return templateRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<EmailTemplate> getTemplatesByTypeAndOrganization(String type, Long organizationId) {
        return templateRepository.findActiveByTypeAndOrganization(type, organizationId);
    }

    @Transactional(readOnly = true)
    public Optional<EmailTemplate> getDefaultTemplateByTypeAndOrganization(String type, Long organizationId) {
        return templateRepository.findDefaultByTypeAndOrganization(type, organizationId);
    }

    @Transactional
    public EmailTemplate updateTemplate(Long id, EmailTemplate templateDetails) {
        EmailTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));

        if (templateDetails.getName() != null) {
            template.setName(templateDetails.getName());
        }
        if (templateDetails.getDescription() != null) {
            template.setDescription(templateDetails.getDescription());
        }
        if (templateDetails.getSubject() != null) {
            template.setSubject(templateDetails.getSubject());
        }
        if (templateDetails.getBody() != null) {
            template.setBody(templateDetails.getBody());
        }
        if (templateDetails.getActive() != null) {
            template.setActive(templateDetails.getActive());
        }
        if (templateDetails.getIsDefault() != null) {
            template.setIsDefault(templateDetails.getIsDefault());
        }
        if (templateDetails.getAvailableVariables() != null) {
            template.setAvailableVariables(templateDetails.getAvailableVariables());
        }

        return templateRepository.save(template);
    }

    @Transactional
    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template not found with id: " + id);
        }
        templateRepository.deleteById(id);
    }
}

