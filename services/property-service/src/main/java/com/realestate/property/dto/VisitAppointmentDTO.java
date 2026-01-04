package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO pour un rendez-vous de visite")
public class VisitAppointmentDTO {

    @Schema(description = "ID du rendez-vous")
    private Long id;

    @Schema(description = "ID de la propriété")
    private Long propertyId;

    @Schema(description = "Titre de la propriété (pour affichage)")
    private String propertyTitle;

    @Schema(description = "ID de l'utilisateur")
    private Long userId;

    @Schema(description = "ID de l'agent")
    private Long agentId;

    @Schema(description = "Nom de l'agent (pour affichage)")
    private String agentName;

    @Schema(description = "Nom du visiteur")
    private String visitorName;

    @Schema(description = "Email du visiteur")
    private String visitorEmail;

    @Schema(description = "Téléphone du visiteur")
    private String visitorPhone;

    @Schema(description = "Date et heure du rendez-vous")
    private LocalDateTime appointmentDate;

    @Schema(description = "Durée estimée en minutes")
    private Integer durationMinutes;

    @Schema(description = "Statut (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)")
    private String status;

    @Schema(description = "Notes additionnelles")
    private String notes;

    @Schema(description = "Notes de l'agent")
    private String agentNotes;

    @Schema(description = "Rappel envoyé")
    private Boolean reminderSent;

    @Schema(description = "Date d'envoi du rappel")
    private LocalDateTime reminderSentAt;

    @Schema(description = "Date de confirmation")
    private LocalDateTime confirmedAt;

    @Schema(description = "Date d'annulation")
    private LocalDateTime cancelledAt;

    @Schema(description = "Raison de l'annulation")
    private String cancellationReason;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de mise à jour")
    private LocalDateTime updatedAt;

    // Constructors
    public VisitAppointmentDTO() {
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

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorEmail() {
        return visitorEmail;
    }

    public void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    public void setVisitorPhone(String visitorPhone) {
        this.visitorPhone = visitorPhone;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAgentNotes() {
        return agentNotes;
    }

    public void setAgentNotes(String agentNotes) {
        this.agentNotes = agentNotes;
    }

    public Boolean getReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(Boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public LocalDateTime getReminderSentAt() {
        return reminderSentAt;
    }

    public void setReminderSentAt(LocalDateTime reminderSentAt) {
        this.reminderSentAt = reminderSentAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
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

