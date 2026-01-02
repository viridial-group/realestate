package com.realestate.workflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approval_workflows", indexes = {
    @Index(name = "idx_workflow_org", columnList = "organization_id"),
    @Index(name = "idx_workflow_action", columnList = "action"),
    @Index(name = "idx_workflow_active", columnList = "active"),
    @Index(name = "idx_workflow_status", columnList = "status")
})
public class ApprovalWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name; // Nom du workflow

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String action; // PROPERTY_CREATE, PROPERTY_UPDATE, PROPERTY_DELETE, etc.

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy; // Utilisateur qui a créé le workflow

    @Column(name = "target_type", nullable = false)
    @Size(max = 100)
    private String targetType; // PROPERTY, RESOURCE, DOCUMENT, etc.

    @Column(name = "target_id")
    private Long targetId; // ID de la ressource cible (optionnel pour workflows génériques)

    // Configuration du workflow (JSON)
    @Column(columnDefinition = "TEXT")
    private String steps; // JSON array des étapes du workflow

    @Column(name = "required_roles", columnDefinition = "TEXT")
    private String requiredRoles; // JSON array des rôles requis pour chaque étape

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean isDefault = false; // Workflow par défaut pour cette action

    @Size(max = 50)
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED

    // Relations
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public ApprovalWorkflow() {
    }

    public ApprovalWorkflow(String name, String action, String targetType, Long organizationId) {
        this.name = name;
        this.action = action;
        this.targetType = targetType;
        this.organizationId = organizationId;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getRequiredRoles() {
        return requiredRoles;
    }

    public void setRequiredRoles(String requiredRoles) {
        this.requiredRoles = requiredRoles;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
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

