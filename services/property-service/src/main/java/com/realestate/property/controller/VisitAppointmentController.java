package com.realestate.property.controller;

import com.realestate.property.dto.VisitAppointmentCreateDTO;
import com.realestate.property.dto.VisitAppointmentDTO;
import com.realestate.property.service.VisitAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
@Tag(name = "Visit Appointments", description = "Visit appointment management API")
public class VisitAppointmentController {

    private final VisitAppointmentService visitAppointmentService;

    public VisitAppointmentController(VisitAppointmentService visitAppointmentService) {
        this.visitAppointmentService = visitAppointmentService;
    }

    @PostMapping
    @Operation(summary = "Create visit appointment", description = "Creates a new visit appointment")
    public ResponseEntity<VisitAppointmentDTO> createVisit(@Valid @RequestBody VisitAppointmentCreateDTO createDTO) {
        Long userId = getCurrentUserId();
        
        VisitAppointmentDTO created = visitAppointmentService.createVisitAppointment(createDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get visits by property", description = "Returns all visits for a property")
    public ResponseEntity<List<VisitAppointmentDTO>> getVisitsByProperty(
            @PathVariable Long propertyId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        
        if (size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            Page<VisitAppointmentDTO> visitsPage = visitAppointmentService.getVisitsByPropertyId(propertyId, pageable);
            return ResponseEntity.ok(visitsPage.getContent());
        } else {
            List<VisitAppointmentDTO> visits = visitAppointmentService.getVisitsByPropertyId(propertyId);
            return ResponseEntity.ok(visits);
        }
    }

    @GetMapping("/property/{propertyId}/upcoming")
    @Operation(summary = "Get upcoming visits by property", description = "Returns upcoming visits for a property")
    public ResponseEntity<List<VisitAppointmentDTO>> getUpcomingVisitsByProperty(@PathVariable Long propertyId) {
        List<VisitAppointmentDTO> visits = visitAppointmentService.getUpcomingVisitsByPropertyId(propertyId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/my-visits")
    @Operation(summary = "Get my visits", description = "Returns all visits for the current user")
    public ResponseEntity<List<VisitAppointmentDTO>> getMyVisits() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<VisitAppointmentDTO> visits = visitAppointmentService.getVisitsByUserId(userId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/agent/{agentId}")
    @Operation(summary = "Get visits by agent", description = "Returns all visits for an agent")
    public ResponseEntity<List<VisitAppointmentDTO>> getVisitsByAgent(
            @PathVariable Long agentId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        
        if (size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            Page<VisitAppointmentDTO> visitsPage = visitAppointmentService.getVisitsByAgentId(agentId, pageable);
            return ResponseEntity.ok(visitsPage.getContent());
        } else {
            List<VisitAppointmentDTO> visits = visitAppointmentService.getVisitsByAgentId(agentId);
            return ResponseEntity.ok(visits);
        }
    }

    @GetMapping("/agent/{agentId}/upcoming")
    @Operation(summary = "Get upcoming visits by agent", description = "Returns upcoming visits for an agent")
    public ResponseEntity<List<VisitAppointmentDTO>> getUpcomingVisitsByAgent(@PathVariable Long agentId) {
        List<VisitAppointmentDTO> visits = visitAppointmentService.getUpcomingVisitsByAgentId(agentId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/agent/{agentId}/availability")
    @Operation(summary = "Check time slot availability", description = "Checks if a time slot is available for an agent")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long agentId,
            @RequestParam String startTime,
            @RequestParam(required = false, defaultValue = "60") int durationMinutes) {
        
        LocalDateTime start = LocalDateTime.parse(startTime);
        boolean available = visitAppointmentService.isTimeSlotAvailable(agentId, start, durationMinutes);
        return ResponseEntity.ok(available);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm visit", description = "Confirms a visit appointment")
    public ResponseEntity<VisitAppointmentDTO> confirmVisit(@PathVariable Long id) {
        VisitAppointmentDTO updated = visitAppointmentService.confirmVisit(id);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel visit", description = "Cancels a visit appointment")
    public ResponseEntity<VisitAppointmentDTO> cancelVisit(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        VisitAppointmentDTO updated = visitAppointmentService.cancelVisit(id, reason);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Complete visit", description = "Marks a visit as completed")
    public ResponseEntity<VisitAppointmentDTO> completeVisit(
            @PathVariable Long id,
            @RequestParam(required = false) String agentNotes) {
        VisitAppointmentDTO updated = visitAppointmentService.completeVisit(id, agentNotes);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update visit appointment", description = "Updates a visit appointment")
    public ResponseEntity<VisitAppointmentDTO> updateVisit(
            @PathVariable Long id,
            @Valid @RequestBody com.realestate.property.dto.VisitAppointmentUpdateDTO updateDTO) {
        VisitAppointmentDTO updated = visitAppointmentService.updateVisit(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get visit by ID", description = "Returns a visit appointment by its ID")
    public ResponseEntity<VisitAppointmentDTO> getVisitById(@PathVariable Long id) {
        VisitAppointmentDTO visit = visitAppointmentService.getVisitById(id);
        return ResponseEntity.ok(visit);
    }

    @PostMapping("/{id}/exchange")
    @Operation(summary = "Propose visit exchange", description = "Proposes a new date/time for a visit appointment")
    public ResponseEntity<VisitAppointmentDTO> proposeExchange(
            @PathVariable Long id,
            @RequestBody com.realestate.property.dto.VisitExchangeRequestDTO exchangeRequest) {
        exchangeRequest.setVisitId(id);
        VisitAppointmentDTO updated = visitAppointmentService.proposeExchange(
                id,
                exchangeRequest.getProposedDate(),
                exchangeRequest.getMessage()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete visit", description = "Deletes a visit appointment")
    public ResponseEntity<Void> deleteVisit(@PathVariable Long id) {
        visitAppointmentService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer l'ID de l'utilisateur actuellement connecté
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // TODO: Implémenter l'extraction de l'ID utilisateur depuis le token JWT
            return null;
        }
        return null;
    }
}

