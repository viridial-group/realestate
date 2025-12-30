package com.realestate.emailing.controller;

import com.realestate.emailing.dto.EmailTemplateDTO;
import com.realestate.emailing.entity.EmailTemplate;
import com.realestate.emailing.mapper.EmailTemplateMapper;
import com.realestate.emailing.service.EmailTemplateService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emails/templates")
@Tag(name = "Email Templates", description = "Email template management API for multi-tenant email templates")
public class EmailTemplateController {

    private final EmailTemplateService templateService;
    private final EmailTemplateMapper templateMapper;

    public EmailTemplateController(EmailTemplateService templateService, EmailTemplateMapper templateMapper) {
        this.templateService = templateService;
        this.templateMapper = templateMapper;
    }

    @PostMapping
    @Operation(summary = "Create template", description = "Creates a new email template")
    public ResponseEntity<EmailTemplateDTO> createTemplate(@Valid @RequestBody EmailTemplateDTO templateDTO) {
        EmailTemplate template = templateMapper.toEntity(templateDTO);
        EmailTemplate created = templateService.createTemplate(template);
        return ResponseEntity.status(HttpStatus.CREATED).body(templateMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID", description = "Returns template information for a specific template ID")
    public ResponseEntity<EmailTemplateDTO> getTemplateById(@PathVariable Long id) {
        EmailTemplate template = templateService.getTemplateById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmailTemplate", id));
        return ResponseEntity.ok(templateMapper.toDTO(template));
    }

    @GetMapping
    @Operation(summary = "List templates", description = "Returns a list of templates filtered by type and organization")
    public ResponseEntity<List<EmailTemplateDTO>> getTemplates(
            @RequestParam String type,
            @RequestParam(required = false) Long organizationId) {
        List<EmailTemplate> templates;

        if (organizationId != null) {
            templates = templateService.getTemplatesByTypeAndOrganization(type, organizationId);
        } else {
            templates = templateService.getTemplatesByType(type);
        }

        List<EmailTemplateDTO> templateDTOs = templates.stream()
                .map(templateMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(templateDTOs);
    }

    @GetMapping("/default")
    @Operation(summary = "Get default template", description = "Returns the default template for a type and organization")
    public ResponseEntity<EmailTemplateDTO> getDefaultTemplate(
            @RequestParam String type,
            @RequestParam Long organizationId) {
        EmailTemplate template = templateService.getDefaultTemplateByTypeAndOrganization(type, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("EmailTemplate", "default for type " + type + " and organization " + organizationId));
        return ResponseEntity.ok(templateMapper.toDTO(template));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update template", description = "Updates template information for a specific template ID")
    public ResponseEntity<EmailTemplateDTO> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody EmailTemplateDTO templateDTO) {
        EmailTemplate template = templateMapper.toEntity(templateDTO);
        EmailTemplate updated = templateService.updateTemplate(id, template);
        return ResponseEntity.ok(templateMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete template", description = "Deletes a template from the database by ID")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
