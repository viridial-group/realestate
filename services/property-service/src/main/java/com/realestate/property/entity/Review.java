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
@Table(name = "reviews", indexes = {
    @Index(name = "idx_review_property", columnList = "property_id"),
    @Index(name = "idx_review_user", columnList = "user_id"),
    @Index(name = "idx_review_status", columnList = "status"),
    @Index(name = "idx_review_created_at", columnList = "created_at")
})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "user_id")
    private Long userId; // ID de l'utilisateur qui a laissé l'avis (peut être null pour avis anonymes)

    @Size(max = 255)
    @Column(name = "author_name")
    private String authorName; // Nom de l'auteur (pour avis anonymes ou si l'utilisateur n'existe plus)

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

    @Column(name = "verified_purchase")
    private Boolean verifiedPurchase = false; // Indique si l'avis provient d'un achat vérifié

    @Column(name = "helpful_count")
    private Integer helpfulCount = 0; // Nombre de personnes qui ont trouvé l'avis utile

    @Column(name = "active")
    private Boolean active = true; // Pour soft delete

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Review() {
    }

    public Review(Long propertyId, Long userId, String authorName, Integer rating, String comment) {
        this.propertyId = propertyId;
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

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
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

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
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

