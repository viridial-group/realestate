package com.realestate.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "organization_reviews", indexes = {
    @Index(name = "idx_org_review_organization", columnList = "organization_id"),
    @Index(name = "idx_org_review_user", columnList = "user_id"),
    @Index(name = "idx_org_review_status", columnList = "status"),
    @Index(name = "idx_org_review_created_at", columnList = "created_at")
})
public class OrganizationReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "user_id")
    private Long userId; // ID de l'utilisateur qui a laissé l'avis (peut être null pour avis anonymes)

    @Size(max = 255)
    @Column(name = "author_name")
    private String authorName; // Nom de l'auteur (pour avis anonymes)

    @Size(max = 255)
    @Column(name = "author_email")
    private String authorEmail; // Email de l'auteur (optionnel)

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating; // Note de 1 à 5

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment; // Commentaire de l'avis

    @Size(max = 50)
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "verified_client")
    private Boolean verifiedClient = false; // Indique si l'avis provient d'un client vérifié

    @Column(name = "helpful_count")
    private Integer helpfulCount = 0; // Nombre de personnes qui ont trouvé l'avis utile

    @Column(name = "transaction_type")
    @Size(max = 50)
    private String transactionType; // SALE, RENT, ou null pour général

    @Column(name = "active")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public OrganizationReview() {
    }

    public OrganizationReview(Long organizationId, Long userId, String authorName, Integer rating, String comment) {
        this.organizationId = organizationId;
        this.userId = userId;
        this.authorName = authorName;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getVerifiedClient() {
        return verifiedClient;
    }

    public void setVerifiedClient(Boolean verifiedClient) {
        this.verifiedClient = verifiedClient;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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

