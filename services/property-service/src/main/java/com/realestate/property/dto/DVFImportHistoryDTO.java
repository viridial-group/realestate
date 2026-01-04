package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Historique d'import DVF")
public class DVFImportHistoryDTO {

    @Schema(description = "ID de l'import")
    private Long id;

    @Schema(description = "Année")
    private String year;

    @Schema(description = "Code département")
    private String department;

    @Schema(description = "Statut (EN_COURS, TERMINÉ, ERREUR)")
    private String status;

    @Schema(description = "Nombre de transactions importées")
    private Long transactionCount;

    @Schema(description = "Message d'erreur si échec")
    private String errorMessage;

    @Schema(description = "ID de l'utilisateur qui a démarré l'import")
    private Long startedBy;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de mise à jour")
    private LocalDateTime updatedAt;

    @Schema(description = "Date de complétion")
    private LocalDateTime completedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getStartedBy() {
        return startedBy;
    }

    public void setStartedBy(Long startedBy) {
        this.startedBy = startedBy;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

