package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO pour un avis d'agence")
public class OrganizationReviewDTO {

    @Schema(description = "ID de l'avis")
    private Long id;

    @Schema(description = "ID de l'organisation")
    private Long organizationId;

    @Schema(description = "ID de l'utilisateur")
    private Long userId;

    @Schema(description = "Nom de l'auteur")
    private String authorName;

    @Schema(description = "Email de l'auteur")
    private String authorEmail;

    @Schema(description = "Note de 1 à 5")
    private Integer rating;

    @Schema(description = "Commentaire")
    private String comment;

    @Schema(description = "Statut (PENDING, APPROVED, REJECTED)")
    private String status;

    @Schema(description = "Client vérifié")
    private Boolean verifiedClient;

    @Schema(description = "Nombre de personnes ayant trouvé l'avis utile")
    private Integer helpfulCount;

    @Schema(description = "Type de transaction (SALE, RENT)")
    private String transactionType;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de mise à jour")
    private LocalDateTime updatedAt;

    // Constructors
    public OrganizationReviewDTO() {
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

