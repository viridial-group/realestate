package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO pour un avis")
public class ReviewDTO {

    @Schema(description = "ID de l'avis")
    private Long id;

    @Schema(description = "ID de la propriété")
    private Long propertyId;

    @Schema(description = "Titre de la propriété (pour affichage)")
    private String propertyTitle;

    @Schema(description = "ID de l'utilisateur")
    private Long userId;

    @Schema(description = "Nom de l'auteur")
    private String authorName;

    @Schema(description = "Email de l'auteur")
    private String authorEmail;

    @Schema(description = "Note (1-5)")
    private Integer rating;

    @Schema(description = "Commentaire")
    private String comment;

    @Schema(description = "Statut (PENDING, APPROVED, REJECTED)")
    private String status;

    @Schema(description = "Achat vérifié")
    private Boolean verifiedPurchase;

    @Schema(description = "Nombre de personnes ayant trouvé l'avis utile")
    private Integer helpfulCount;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de mise à jour")
    private LocalDateTime updatedAt;

    // Constructors
    public ReviewDTO() {
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

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
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

