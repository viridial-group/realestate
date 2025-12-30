package com.realestate.emailing.controller;

import com.realestate.emailing.entity.Email;
import com.realestate.emailing.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/emails")
@Tag(name = "Emails", description = "Email management API for transactional and workflow-triggered emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    @Operation(summary = "Create and send email", description = "Creates a new email and sends it immediately")
    public ResponseEntity<Email> sendEmail(@Valid @RequestBody Email email) {
        Email sent = emailService.sendEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(sent);
    }

    @PostMapping("/template")
    @Operation(summary = "Send email from template", description = "Sends an email using a template with variables")
    public ResponseEntity<Email> sendEmailFromTemplate(@RequestBody Map<String, Object> request) {
        try {
            String templateName = request.get("templateName").toString();
            String recipientEmail = request.get("recipientEmail").toString();
            Long recipientId = request.containsKey("recipientId") ? Long.valueOf(request.get("recipientId").toString()) : null;
            Long organizationId = Long.valueOf(request.get("organizationId").toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = request.containsKey("variables") ? (Map<String, Object>) request.get("variables") : null;

            Email sent = emailService.sendEmailFromTemplate(templateName, recipientEmail, recipientId, organizationId, variables);
            return ResponseEntity.status(HttpStatus.CREATED).body(sent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get email by ID", description = "Returns email information for a specific email ID")
    public ResponseEntity<Email> getEmailById(@PathVariable Long id) {
        return emailService.getEmailById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List emails", description = "Returns a list of emails filtered by recipient or organization")
    public ResponseEntity<List<Email>> getEmails(
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

        return ResponseEntity.ok(emails);
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "Retry failed email", description = "Retries sending a failed email")
    public ResponseEntity<Email> retryEmail(@PathVariable Long id) {
        try {
            Email retried = emailService.retryFailedEmail(id);
            return ResponseEntity.ok(retried);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

