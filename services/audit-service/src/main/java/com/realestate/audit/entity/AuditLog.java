package com.realestate.audit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_actor", columnList = "actor_id"),
    @Index(name = "idx_audit_org", columnList = "organization_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_target", columnList = "target_type, target_id"),
    @Index(name = "idx_audit_created", columnList = "created_at")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_id")
    private Long actorId; // Utilisateur qui a effectué l'action

    @Column(name = "actor_email")
    @Size(max = 255)
    private String actorEmail; // Email de l'acteur (pour traçabilité même si utilisateur supprimé)

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, APPROVE, REJECT, etc.

    @Size(max = 100)
    @Column(name = "target_type")
    private String targetType; // PROPERTY, RESOURCE, DOCUMENT, USER, etc.

    @Column(name = "target_id")
    private Long targetId; // ID de la ressource cible

    @Size(max = 50)
    private String status; // SUCCESS, FAILURE, PARTIAL

    @Size(max = 1000)
    private String description; // Description de l'action

    @Column(name = "ip_address")
    @Size(max = 45)
    private String ipAddress; // Adresse IP de l'acteur

    @Column(name = "user_agent")
    @Size(max = 500)
    private String userAgent; // User-Agent du navigateur/client

    @Column(name = "request_method")
    @Size(max = 10)
    private String requestMethod; // GET, POST, PUT, DELETE, etc.

    @Column(name = "request_path")
    @Size(max = 500)
    private String requestPath; // Chemin de la requête

    // Métadonnées JSON pour informations supplémentaires
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON pour stocker des métadonnées (champs modifiés, anciennes valeurs, etc.)

    @Size(max = 1000)
    private String errorMessage; // Message d'erreur en cas d'échec

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public AuditLog() {
    }

    public AuditLog(Long actorId, Long organizationId, String action) {
        this.actorId = actorId;
        this.organizationId = organizationId;
        this.action = action;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

