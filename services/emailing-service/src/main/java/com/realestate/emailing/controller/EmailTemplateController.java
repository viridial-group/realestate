package com.realestate.emailing.controller;

import com.realestate.emailing.entity.EmailTemplate;
import com.realestate.emailing.service.EmailTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/emails/templates")
@Tag(name = "Email Templates", description = "Email template management API for multi-tenant email templates")
public class EmailTemplateController {

    private final EmailTemplateService templateService;

    public EmailTemplateController(EmailTemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    @Operation(summary = "Create template", description = "Creates a new email template")
    public ResponseEntity<EmailTemplate> createTemplate(@Valid @RequestBody EmailTemplate template) {
        EmailTemplate created = templateService.createTemplate(template);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID", description = "Returns template information for a specific template ID")
    public ResponseEntity<EmailTemplate> getTemplateById(@PathVariable Long id) {
        return templateService.getTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List templates", description = "Returns a list of templates filtered by type and organization")
    public ResponseEntity<List<EmailTemplate>> getTemplates(
            @RequestParam String type,
            @RequestParam(required = false) Long organizationId) {
        List<EmailTemplate> templates;

        if (organizationId != null) {
            templates = templateService.getTemplatesByTypeAndOrganization(type, organizationId);
        } else {
            templates = templateService.getTemplatesByType(type);
        }

        return ResponseEntity.ok(templates);
    }

    @GetMapping("/default")
    @Operation(summary = "Get default template", description = "Returns the default template for a type and organization")
    public ResponseEntity<EmailTemplate> getDefaultTemplate(
            @RequestParam String type,
            @RequestParam Long organizationId) {
        return templateService.getDefaultTemplateByTypeAndOrganization(type, organizationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update template", description = "Updates template information for a specific template ID")
    public ResponseEntity<EmailTemplate> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody EmailTemplate templateDetails) {
        try {
            EmailTemplate updated = templateService.updateTemplate(id, templateDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete template", description = "Deletes a template from the database by ID")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

