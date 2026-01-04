package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "DTO pour créer un rendez-vous de visite")
public class VisitAppointmentCreateDTO {

    @NotNull
    @Schema(description = "ID de la propriété", required = true)
    private Long propertyId;

    @NotNull
    @Schema(description = "ID de l'agent", required = true)
    private Long agentId;

    @NotNull
    @Schema(description = "Date et heure du rendez-vous", required = true)
    private LocalDateTime appointmentDate;

    @Schema(description = "Durée estimée en minutes (par défaut: 60)")
    private Integer durationMinutes = 60;

    @Schema(description = "Nom du visiteur (requis si userId n'est pas fourni)")
    private String visitorName;

    @Schema(description = "Email du visiteur")
    private String visitorEmail;

    @Schema(description = "Téléphone du visiteur")
    private String visitorPhone;

    @Schema(description = "Notes additionnelles")
    private String notes;

    // Constructors
    public VisitAppointmentCreateDTO() {
    }

    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
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
}

