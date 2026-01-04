package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO pour mettre à jour un rendez-vous de visite")
public class VisitAppointmentUpdateDTO {

    @Schema(description = "Date et heure du rendez-vous")
    private LocalDateTime appointmentDate;

    @Schema(description = "Durée estimée en minutes")
    private Integer durationMinutes;

    @Schema(description = "Nom du visiteur")
    private String visitorName;

    @Schema(description = "Email du visiteur")
    private String visitorEmail;

    @Schema(description = "Téléphone du visiteur")
    private String visitorPhone;

    @Schema(description = "Notes additionnelles")
    private String notes;

    @Schema(description = "Notes de l'agent")
    private String agentNotes;

    @Schema(description = "Statut (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)")
    private String status;

    @Schema(description = "Raison de l'annulation (si annulé)")
    private String cancellationReason;

    // Constructors
    public VisitAppointmentUpdateDTO() {
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}

