package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO pour créer un avis d'agence")
public class OrganizationReviewCreateDTO {

    @NotNull(message = "L'ID de l'organisation est requis")
    @Schema(description = "ID de l'organisation", required = true)
    private Long organizationId;

    @Schema(description = "ID de l'utilisateur (optionnel si authorName est fourni)")
    private Long userId;

    @Size(max = 255, message = "Le nom de l'auteur ne peut pas dépasser 255 caractères")
    @Schema(description = "Nom de l'auteur (requis si userId n'est pas fourni)")
    private String authorName;

    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères")
    @Schema(description = "Email de l'auteur (optionnel)")
    private String authorEmail;

    @NotNull(message = "La note est requise")
    @Min(value = 1, message = "La note doit être au moins 1")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    @Schema(description = "Note de 1 à 5", required = true, example = "5")
    private Integer rating;

    @NotBlank(message = "Le commentaire est requis")
    @Size(max = 2000, message = "Le commentaire ne peut pas dépasser 2000 caractères")
    @Schema(description = "Commentaire de l'avis", required = true)
    private String comment;

    @Schema(description = "Indique si l'avis provient d'un client vérifié", example = "false")
    private Boolean verifiedClient = false;

    @Size(max = 50)
    @Schema(description = "Type de transaction (SALE, RENT)", example = "SALE")
    private String transactionType;

    // Constructors
    public OrganizationReviewCreateDTO() {
    }

    // Getters and Setters
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

    public Boolean getVerifiedClient() {
        return verifiedClient;
    }

    public void setVerifiedClient(Boolean verifiedClient) {
        this.verifiedClient = verifiedClient;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}

