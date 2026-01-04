package com.realestate.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_appointments", indexes = {
    @Index(name = "idx_visit_property", columnList = "property_id"),
    @Index(name = "idx_visit_user", columnList = "user_id"),
    @Index(name = "idx_visit_agent", columnList = "agent_id"),
    @Index(name = "idx_visit_date", columnList = "appointment_date"),
    @Index(name = "idx_visit_status", columnList = "status")
})
public class VisitAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "user_id")
    private Long userId; // ID de l'utilisateur qui demande la visite (peut être null pour visites anonymes)

    @NotNull
    @Column(name = "agent_id", nullable = false)
    private Long agentId; // ID de l'agent qui effectuera la visite

    @Size(max = 255)
    @Column(name = "visitor_name")
    private String visitorName; // Nom du visiteur (pour visites anonymes)

    @Size(max = 255)
    @Column(name = "visitor_email")
    private String visitorEmail; // Email du visiteur

    @Size(max = 20)
    @Column(name = "visitor_phone")
    private String visitorPhone; // Téléphone du visiteur

    @NotNull
    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate; // Date et heure du rendez-vous

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 60; // Durée estimée en minutes

    @Size(max = 50)
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW

    @Size(max = 1000)
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Notes additionnelles

    @Size(max = 1000)
    @Column(name = "agent_notes", columnDefinition = "TEXT")
    private String agentNotes; // Notes de l'agent après la visite

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false; // Indique si un rappel a été envoyé

    @Column(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt; // Date d'envoi du rappel

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt; // Date de confirmation

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt; // Date d'annulation

    @Size(max = 500)
    @Column(name = "cancellation_reason")
    private String cancellationReason; // Raison de l'annulation

    @Column(name = "active", nullable = false)
    private Boolean active = true; // Pour soft delete

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public VisitAppointment() {
    }

    public VisitAppointment(Long propertyId, Long agentId, LocalDateTime appointmentDate) {
        this.propertyId = propertyId;
        this.agentId = agentId;
        this.appointmentDate = appointmentDate;
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

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
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

