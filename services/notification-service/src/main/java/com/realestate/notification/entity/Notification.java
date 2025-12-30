package com.realestate.notification.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_recipient", columnList = "recipient_id"),
    @Index(name = "idx_notification_status", columnList = "status"),
    @Index(name = "idx_notification_type", columnList = "type"),
    @Index(name = "idx_notification_org", columnList = "organization_id"),
    @Index(name = "idx_notification_created", columnList = "created_at")
})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String type; // INFO, WARNING, ERROR, SUCCESS, APPROVAL_REQUEST, etc.

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @Size(max = 2000)
    private String message;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId; // Utilisateur destinataire

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "sender_id")
    private Long senderId; // Utilisateur expéditeur (optionnel)

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, SENT, READ, ARCHIVED

    @Size(max = 100)
    private String channel; // IN_APP, PUSH, SMS, EMAIL

    @Column(name = "target_type")
    @Size(max = 100)
    private String targetType; // PROPERTY, RESOURCE, TASK, etc.

    @Column(name = "target_id")
    private Long targetId; // ID de la ressource cible

    @Column(name = "action_url")
    @Size(max = 500)
    private String actionUrl; // URL d'action (optionnel)

    @Column(name = "read_at")
    private LocalDateTime readAt; // Date de lecture

    @Column(nullable = false)
    private Boolean active = true;

    // Métadonnées JSON pour informations supplémentaires
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON pour stocker des métadonnées

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Notification() {
    }

    public Notification(String type, String title, String message, Long recipientId, Long organizationId) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.recipientId = recipientId;
        this.organizationId = organizationId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

