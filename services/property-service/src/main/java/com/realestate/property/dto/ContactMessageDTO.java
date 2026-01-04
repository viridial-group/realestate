package com.realestate.property.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ContactMessageDTO {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(max = 500)
    private String subject;

    @NotBlank
    private String message;

    private String status = "NEW";

    private Long propertyId;

    private String propertyTitle; // Titre de la propriété (pour affichage)

    private String propertyReference; // Référence de la propriété

    private Long organizationId;

    private LocalDateTime readAt;

    private Long readBy;

    private String readByName; // Nom de l'utilisateur qui a lu

    private LocalDateTime repliedAt;

    private Long repliedBy;

    private String repliedByName; // Nom de l'utilisateur qui a répondu

    private String notes;

    private Boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public ContactMessageDTO() {
    }

    public ContactMessageDTO(String name, String email, String subject, String message) {
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

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public String getPropertyReference() {
        return propertyReference;
    }

    public void setPropertyReference(String propertyReference) {
        this.propertyReference = propertyReference;
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

    public String getReadByName() {
        return readByName;
    }

    public void setReadByName(String readByName) {
        this.readByName = readByName;
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

    public String getRepliedByName() {
        return repliedByName;
    }

    public void setRepliedByName(String repliedByName) {
        this.repliedByName = repliedByName;
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

