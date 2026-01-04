package com.realestate.property.mapper;

import com.realestate.property.dto.VisitAppointmentCreateDTO;
import com.realestate.property.dto.VisitAppointmentDTO;
import com.realestate.property.entity.VisitAppointment;
import org.springframework.stereotype.Component;

@Component
public class VisitAppointmentMapper {

    public VisitAppointmentDTO toDTO(VisitAppointment visitAppointment) {
        if (visitAppointment == null) {
            return null;
        }

        VisitAppointmentDTO dto = new VisitAppointmentDTO();
        dto.setId(visitAppointment.getId());
        dto.setPropertyId(visitAppointment.getPropertyId());
        dto.setUserId(visitAppointment.getUserId());
        dto.setAgentId(visitAppointment.getAgentId());
        dto.setVisitorName(visitAppointment.getVisitorName());
        dto.setVisitorEmail(visitAppointment.getVisitorEmail());
        dto.setVisitorPhone(visitAppointment.getVisitorPhone());
        dto.setAppointmentDate(visitAppointment.getAppointmentDate());
        dto.setDurationMinutes(visitAppointment.getDurationMinutes());
        dto.setStatus(visitAppointment.getStatus());
        dto.setNotes(visitAppointment.getNotes());
        dto.setAgentNotes(visitAppointment.getAgentNotes());
        dto.setReminderSent(visitAppointment.getReminderSent());
        dto.setReminderSentAt(visitAppointment.getReminderSentAt());
        dto.setConfirmedAt(visitAppointment.getConfirmedAt());
        dto.setCancelledAt(visitAppointment.getCancelledAt());
        dto.setCancellationReason(visitAppointment.getCancellationReason());
        dto.setCreatedAt(visitAppointment.getCreatedAt());
        dto.setUpdatedAt(visitAppointment.getUpdatedAt());
        // propertyTitle and agentName will be set by service

        return dto;
    }

    public VisitAppointment toEntity(VisitAppointmentCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        VisitAppointment visitAppointment = new VisitAppointment();
        visitAppointment.setPropertyId(createDTO.getPropertyId());
        visitAppointment.setAgentId(createDTO.getAgentId());
        visitAppointment.setAppointmentDate(createDTO.getAppointmentDate());
        visitAppointment.setDurationMinutes(createDTO.getDurationMinutes() != null ? createDTO.getDurationMinutes() : 60);
        visitAppointment.setVisitorName(createDTO.getVisitorName());
        visitAppointment.setVisitorEmail(createDTO.getVisitorEmail());
        visitAppointment.setVisitorPhone(createDTO.getVisitorPhone());
        visitAppointment.setNotes(createDTO.getNotes());
        visitAppointment.setStatus("PENDING");
        visitAppointment.setReminderSent(false);
        visitAppointment.setActive(true);

        return visitAppointment;
    }
}

