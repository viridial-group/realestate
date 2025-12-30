package com.realestate.emailing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "emails", indexes = {
    @Index(name = "idx_email_recipient", columnList = "recipient_email"),
    @Index(name = "idx_email_status", columnList = "status"),
    @Index(name = "idx_email_template", columnList = "template_id"),
    @Index(name = "idx_email_org", columnList = "organization_id"),
    @Index(name = "idx_email_sent_at", columnList = "sent_at")
})
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private EmailTemplate template; // Template utilisé (optionnel)

    @NotBlank
    @Size(max = 255)
    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "recipient_id")
    private Long recipientId; // ID utilisateur (optionnel)

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body; // Corps de l'email (HTML ou texte)

    @Size(max = 50)
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, SENT, FAILED, BOUNCED

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "sender_email")
    @Size(max = 255)
    private String senderEmail; // Email expéditeur (optionnel, utilise config par défaut)

    @Column(name = "sent_at")
    private LocalDateTime sentAt; // Date d'envoi

    @Column(name = "failed_at")
    private LocalDateTime failedAt; // Date d'échec

    @Size(max = 1000)
    private String errorMessage; // Message d'erreur en cas d'échec

    @Column(name = "retry_count")
    private Integer retryCount = 0; // Nombre de tentatives

    // Métadonnées JSON pour variables de template
    @Column(columnDefinition = "TEXT")
    private String variables; // JSON pour stocker les variables de template

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Email() {
    }

    public Email(String recipientEmail, String subject, String body, Long organizationId) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
        this.organizationId = organizationId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmailTemplate getTemplate() {
        return template;
    }

    public void setTemplate(EmailTemplate template) {
        this.template = template;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

