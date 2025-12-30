package com.realestate.emailing.controller;

import com.realestate.emailing.dto.EmailDTO;
import com.realestate.emailing.entity.Email;
import com.realestate.emailing.mapper.EmailMapper;
import com.realestate.emailing.service.EmailService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emails")
@Tag(name = "Emails", description = "Email management API for transactional and workflow-triggered emails")
public class EmailController {

    private final EmailService emailService;
    private final EmailMapper emailMapper;

    public EmailController(EmailService emailService, EmailMapper emailMapper) {
        this.emailService = emailService;
        this.emailMapper = emailMapper;
    }

    @PostMapping
    @Operation(summary = "Create and send email", description = "Creates a new email and sends it immediately")
    public ResponseEntity<EmailDTO> sendEmail(@Valid @RequestBody EmailDTO emailDTO) {
        Email email = emailMapper.toEntity(emailDTO);
        Email sent = emailService.sendEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(emailMapper.toDTO(sent));
    }

    @PostMapping("/template")
    @Operation(summary = "Send email from template", description = "Sends an email using a template with variables")
    public ResponseEntity<EmailDTO> sendEmailFromTemplate(@RequestBody Map<String, Object> request) {
        try {
            String templateName = request.get("templateName").toString();
            String recipientEmail = request.get("recipientEmail").toString();
            Long recipientId = request.containsKey("recipientId") ? Long.valueOf(request.get("recipientId").toString()) : null;
            Long organizationId = Long.valueOf(request.get("organizationId").toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = request.containsKey("variables") ? (Map<String, Object>) request.get("variables") : null;

            Email sent = emailService.sendEmailFromTemplate(templateName, recipientEmail, recipientId, organizationId, variables);
            return ResponseEntity.status(HttpStatus.CREATED).body(emailMapper.toDTO(sent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get email by ID", description = "Returns email information for a specific email ID")
    public ResponseEntity<EmailDTO> getEmailById(@PathVariable Long id) {
        Email email = emailService.getEmailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email", id));
        return ResponseEntity.ok(emailMapper.toDTO(email));
    }

    @GetMapping
    @Operation(summary = "List emails", description = "Returns a list of emails filtered by recipient or organization")
    public ResponseEntity<List<EmailDTO>> getEmails(
            @RequestParam(required = false) String recipientEmail,
            @RequestParam(required = false) Long organizationId) {
        List<Email> emails;

        if (recipientEmail != null) {
            emails = emailService.getEmailsByRecipientEmail(recipientEmail);
        } else if (organizationId != null) {
            emails = emailService.getEmailsByOrganizationId(organizationId);
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<EmailDTO> emailDTOs = emails.stream()
                .map(emailMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emailDTOs);
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "Retry failed email", description = "Retries sending a failed email")
    public ResponseEntity<EmailDTO> retryEmail(@PathVariable Long id) {
        Email retried = emailService.retryFailedEmail(id);
        return ResponseEntity.ok(emailMapper.toDTO(retried));
    }
}
