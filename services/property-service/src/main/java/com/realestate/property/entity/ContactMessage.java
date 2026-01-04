package com.realestate.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages", indexes = {
    @Index(name = "idx_contact_email", columnList = "email"),
    @Index(name = "idx_contact_status", columnList = "status"),
    @Index(name = "idx_contact_property", columnList = "property_id"),
    @Index(name = "idx_contact_created", columnList = "created_at")
})
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false)
    private String email;

    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false)
    private String subject;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Size(max = 50)
    @Column(nullable = false)
    private String status = "NEW"; // NEW, READ, REPLIED, ARCHIVED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property; // Propriété concernée (optionnel)

    @Column(name = "property_id", insertable = false, updatable = false)
    private Long propertyId;

    @Column(name = "organization_id")
    private Long organizationId; // Organisation concernée

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "read_by")
    private Long readBy; // ID de l'utilisateur qui a lu le message

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Column(name = "replied_by")
    private Long repliedBy; // ID de l'utilisateur qui a répondu

    @Column(columnDefinition = "TEXT")
    private String notes; // Notes internes sur le message

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public ContactMessage() {
    }

    public ContactMessage(String name, String email, String subject, String message) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
        this.propertyId = property != null ? property.getId() : null;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Long getReadBy() {
        return readBy;
    }

    public void setReadBy(Long readBy) {
        this.readBy = readBy;
    }

    public LocalDateTime getRepliedAt() {
        return repliedAt;
    }

    public void setRepliedAt(LocalDateTime repliedAt) {
        this.repliedAt = repliedAt;
    }

    public Long getRepliedBy() {
        return repliedBy;
    }

    public void setRepliedBy(Long repliedBy) {
        this.repliedBy = repliedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

