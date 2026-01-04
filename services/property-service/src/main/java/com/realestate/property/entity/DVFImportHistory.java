package com.realestate.property.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entité pour tracker l'historique des imports DVF
 */
@Entity
@Table(name = "dvf_import_history", indexes = {
    @Index(name = "idx_dvf_import_year_dept", columnList = "year,department"),
    @Index(name = "idx_dvf_import_status", columnList = "status"),
    @Index(name = "idx_dvf_import_created", columnList = "created_at")
})
public class DVFImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String year;

    @Column(nullable = false, length = 10)
    private String department;

    @Column(nullable = false, length = 50)
    private String status; // EN_COURS, TERMINÉ, ERREUR

    @Column(name = "transaction_count")
    private Long transactionCount;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "started_by")
    private Long startedBy; // ID de l'utilisateur qui a démarré l'import

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Constructors
    public DVFImportHistory() {
    }

    public DVFImportHistory(String year, String department, String status, Long startedBy) {
        this.year = year;
        this.department = department;
        this.status = status;
        this.startedBy = startedBy;
    }

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

