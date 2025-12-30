package com.realestate.document.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_document_resource", columnList = "resource_id"),
    @Index(name = "idx_document_property", columnList = "property_id"),
    @Index(name = "idx_document_org", columnList = "organization_id"),
    @Index(name = "idx_document_created_by", columnList = "created_by")
})
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name; // Nom original du fichier

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String type; // IMAGE, PDF, DOCUMENT, etc.

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String mimeType; // image/jpeg, application/pdf, etc.

    @NotNull
    @Column(nullable = false)
    private Long fileSize; // Taille en bytes

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false)
    private String filePath; // Chemin relatif sur le système de fichiers

    @Size(max = 1000)
    private String url; // URL publique d'accès (optionnel)

    @Column(name = "resource_id")
    private Long resourceId; // Référence à une ressource (optionnel)

    @Column(name = "property_id")
    private Long propertyId; // Référence à une propriété (optionnel)

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy; // Utilisateur qui a uploadé le fichier

    @Column(nullable = false)
    private Boolean active = true;

    // Métadonnées JSON pour informations supplémentaires
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON pour stocker des métadonnées (dimensions image, etc.)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Document() {
    }

    public Document(String name, String type, String mimeType, Long fileSize, String filePath, Long organizationId, Long createdBy) {
        this.name = name;
        this.type = type;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.organizationId = organizationId;
        this.createdBy = createdBy;
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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

