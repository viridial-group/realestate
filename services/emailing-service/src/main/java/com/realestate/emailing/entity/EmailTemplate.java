package com.realestate.emailing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "email_templates", indexes = {
    @Index(name = "idx_template_name", columnList = "name"),
    @Index(name = "idx_template_type", columnList = "type"),
    @Index(name = "idx_template_org", columnList = "organization_id"),
    @Index(name = "idx_template_active", columnList = "active")
})
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name; // Nom du template (ex: "welcome_email", "approval_request")

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String type; // WELCOME, APPROVAL_REQUEST, NOTIFICATION, etc.

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String subject; // Sujet de l'email (peut contenir des variables {{variable}})

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body; // Corps de l'email (HTML avec Thymeleaf)

    @Column(name = "organization_id")
    private Long organizationId; // null = template global, sinon = template spécifique à l'organisation

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean isDefault = false; // Template par défaut pour ce type

    // Variables disponibles dans le template (JSON array)
    @Column(name = "available_variables", columnDefinition = "TEXT")
    private String availableVariables; // JSON array des variables disponibles

    // Relations
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Email> emails = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public EmailTemplate() {
    }

    public EmailTemplate(String name, String type, String subject, String body) {
        this.name = name;
        this.type = type;
        this.subject = subject;
        this.body = body;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getAvailableVariables() {
        return availableVariables;
    }

    public void setAvailableVariables(String availableVariables) {
        this.availableVariables = availableVariables;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
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

