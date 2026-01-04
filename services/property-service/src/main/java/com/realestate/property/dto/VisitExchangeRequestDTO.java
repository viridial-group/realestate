package com.realestate.property.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "DTO pour proposer un échange de créneau de visite")
public class VisitExchangeRequestDTO {

    @NotNull
    @Schema(description = "ID du rendez-vous à échanger", required = true)
    private Long visitId;

    @NotNull
    @Schema(description = "Nouvelle date et heure proposée", required = true)
    private LocalDateTime proposedDate;

    @Schema(description = "Message pour le visiteur expliquant la raison de l'échange")
    private String message;

    // Constructors
    public VisitExchangeRequestDTO() {
    }

    // Getters and Setters
    public Long getVisitId() {
        return visitId;
    }

    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }

    public LocalDateTime getProposedDate() {
        return proposedDate;
    }

    public void setProposedDate(LocalDateTime proposedDate) {
        this.proposedDate = proposedDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

