package com.realestate.property.service;

import com.realestate.property.dto.VisitAppointmentCreateDTO;
import com.realestate.property.dto.VisitAppointmentDTO;
import com.realestate.property.entity.VisitAppointment;
import com.realestate.property.mapper.VisitAppointmentMapper;
import com.realestate.property.repository.PropertyRepository;
import com.realestate.property.repository.VisitAppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitAppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(VisitAppointmentService.class);

    private final VisitAppointmentRepository visitAppointmentRepository;
    private final PropertyRepository propertyRepository;
    private final VisitAppointmentMapper visitAppointmentMapper;

    public VisitAppointmentService(
            VisitAppointmentRepository visitAppointmentRepository,
            PropertyRepository propertyRepository,
            VisitAppointmentMapper visitAppointmentMapper) {
        this.visitAppointmentRepository = visitAppointmentRepository;
        this.propertyRepository = propertyRepository;
        this.visitAppointmentMapper = visitAppointmentMapper;
    }

    /**
     * Créer un nouveau rendez-vous de visite
     */
    @Transactional
    public VisitAppointmentDTO createVisitAppointment(VisitAppointmentCreateDTO createDTO, Long userId) {
        logger.debug("Creating visit appointment for property ID: {} by user ID: {}", createDTO.getPropertyId(), userId);

        // Vérifier que la propriété existe
        propertyRepository.findById(createDTO.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + createDTO.getPropertyId()));

        // Vérifier que la date est dans le futur
        if (createDTO.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date du rendez-vous doit être dans le futur");
        }

        // Vérifier la disponibilité du créneau
        LocalDateTime endTime = createDTO.getAppointmentDate()
                .plusMinutes(createDTO.getDurationMinutes() != null ? createDTO.getDurationMinutes() : 60);
        
        boolean isAvailable = visitAppointmentRepository.isTimeSlotAvailable(
                createDTO.getAgentId(),
                createDTO.getAppointmentDate(),
                endTime
        );

        if (!isAvailable) {
            throw new IllegalArgumentException("Ce créneau n'est pas disponible pour l'agent sélectionné");
        }

        VisitAppointment visitAppointment = visitAppointmentMapper.toEntity(createDTO);
        visitAppointment.setUserId(userId);

        VisitAppointment saved = visitAppointmentRepository.save(visitAppointment);
        logger.info("Visit appointment created with ID: {} for property ID: {}", saved.getId(), createDTO.getPropertyId());

        return enrichDTO(visitAppointmentMapper.toDTO(saved));
    }

    /**
     * Récupérer une visite par son ID
     */
    @Transactional(readOnly = true)
    public VisitAppointmentDTO getVisitById(Long visitId) {
        logger.debug("Fetching visit ID: {}", visitId);
        VisitAppointment visit = visitAppointmentRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found with ID: " + visitId));
        return enrichDTO(visitAppointmentMapper.toDTO(visit));
    }

    /**
     * Récupérer toutes les visites pour une propriété
     */
    @Transactional(readOnly = true)
    public List<VisitAppointmentDTO> getVisitsByPropertyId(Long propertyId) {
        logger.debug("Fetching visits for property ID: {}", propertyId);
        return visitAppointmentRepository.findByPropertyIdAndActiveTrueOrderByAppointmentDateDesc(propertyId).stream()
                .map(visitAppointmentMapper::toDTO)
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer toutes les visites pour une propriété avec pagination
     */
    @Transactional(readOnly = true)
    public Page<VisitAppointmentDTO> getVisitsByPropertyId(Long propertyId, Pageable pageable) {
        logger.debug("Fetching visits for property ID: {} with pagination", propertyId);
        return visitAppointmentRepository.findByPropertyIdAndActiveTrueOrderByAppointmentDateDesc(propertyId, pageable)
                .map(visitAppointmentMapper::toDTO)
                .map(this::enrichDTO);
    }

    /**
     * Récupérer toutes les visites pour un utilisateur
     */
    @Transactional(readOnly = true)
    public List<VisitAppointmentDTO> getVisitsByUserId(Long userId) {
        logger.debug("Fetching visits for user ID: {}", userId);
        return visitAppointmentRepository.findByUserIdAndActiveTrueOrderByAppointmentDateDesc(userId).stream()
                .map(visitAppointmentMapper::toDTO)
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer toutes les visites pour un agent
     */
    @Transactional(readOnly = true)
    public List<VisitAppointmentDTO> getVisitsByAgentId(Long agentId) {
        logger.debug("Fetching visits for agent ID: {}", agentId);
        return visitAppointmentRepository.findByAgentIdAndActiveTrueOrderByAppointmentDateDesc(agentId).stream()
                .map(visitAppointmentMapper::toDTO)
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer toutes les visites pour un agent avec pagination
     */
    @Transactional(readOnly = true)
    public Page<VisitAppointmentDTO> getVisitsByAgentId(Long agentId, Pageable pageable) {
        logger.debug("Fetching visits for agent ID: {} with pagination", agentId);
        return visitAppointmentRepository.findByAgentIdAndActiveTrueOrderByAppointmentDateDesc(agentId, pageable)
                .map(visitAppointmentMapper::toDTO)
                .map(this::enrichDTO);
    }

    /**
     * Récupérer les visites à venir pour une propriété
     */
    @Transactional(readOnly = true)
    public List<VisitAppointmentDTO> getUpcomingVisitsByPropertyId(Long propertyId) {
        logger.debug("Fetching upcoming visits for property ID: {}", propertyId);
        return visitAppointmentRepository.findUpcomingByPropertyId(propertyId, LocalDateTime.now()).stream()
                .map(visitAppointmentMapper::toDTO)
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les visites à venir pour un agent
     */
    @Transactional(readOnly = true)
    public List<VisitAppointmentDTO> getUpcomingVisitsByAgentId(Long agentId) {
        logger.debug("Fetching upcoming visits for agent ID: {}", agentId);
        return visitAppointmentRepository.findUpcomingByAgentId(agentId, LocalDateTime.now()).stream()
                .map(visitAppointmentMapper::toDTO)
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }

    /**
     * Confirmer un rendez-vous
     */
    @Transactional
    public VisitAppointmentDTO confirmVisit(Long visitId) {
        logger.debug("Confirming visit ID: {}", visitId);
        VisitAppointment visit = visitAppointmentRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found with ID: " + visitId));
        
        visit.setStatus("CONFIRMED");
        visit.setConfirmedAt(LocalDateTime.now());
        
        VisitAppointment saved = visitAppointmentRepository.save(visit);
        logger.info("Visit confirmed: ID {}", visitId);
        
        return enrichDTO(visitAppointmentMapper.toDTO(saved));
    }

    /**
     * Annuler un rendez-vous
     */
    @Transactional
    public VisitAppointmentDTO cancelVisit(Long visitId, String reason) {
        logger.debug("Cancelling visit ID: {}", visitId);
        VisitAppointment visit = visitAppointmentRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found with ID: " + visitId));
        
        visit.setStatus("CANCELLED");
        visit.setCancelledAt(LocalDateTime.now());
        visit.setCancellationReason(reason);
        
        VisitAppointment saved = visitAppointmentRepository.save(visit);
        logger.info("Visit cancelled: ID {}", visitId);
        
        return enrichDTO(visitAppointmentMapper.toDTO(saved));
    }

    /**
     * Marquer une visite comme complétée
     */
    @Transactional
    public VisitAppointmentDTO completeVisit(Long visitId, String agentNotes) {
        logger.debug("Completing visit ID: {}", visitId);
        VisitAppointment visit = visitAppointmentRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found with ID: " + visitId));
        
        visit.setStatus("COMPLETED");
        if (agentNotes != null) {
            visit.setAgentNotes(agentNotes);
        }
        
        VisitAppointment saved = visitAppointmentRepository.save(visit);
        logger.info("Visit completed: ID {}", visitId);
        
        return enrichDTO(visitAppointmentMapper.toDTO(saved));
    }

    /**
     * Vérifier la disponibilité d'un créneau
     */
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(Long agentId, LocalDateTime startTime, Integer durationMinutes) {
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes != null ? durationMinutes : 60);
        return visitAppointmentRepository.isTimeSlotAvailable(agentId, startTime, endTime);
    }

    /**
     * Mettre à jour un rendez-vous
     */
    @Transactional
    public VisitAppointmentDTO updateVisit(Long visitId, com.realestate.property.dto.VisitAppointmentUpdateDTO updateDTO) {
        logger.debug("Updating visit ID: {}", visitId);
        VisitAppointment visit = visitAppointmentRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found with ID: " + visitId));

        // Vérifier la disponibilité si la date change
        if (updateDTO.getAppointmentDate() != null && !updateDTO.getAppointmentDate().equals(visit.getAppointmentDate())) {
            LocalDateTime newEndTime = updateDTO.getAppointmentDate()
                    .plusMinutes(updateDTO.getDurationMinutes() != null ? updateDTO.getDurationMinutes() : visit.getDurationMinutes());
            
            // Vérifier la disponibilité (en excluant le rendez-vous actuel)
            boolean isAvailable = visitAppointmentRepository.isTimeSlotAvailable(
                    visit.getAgentId(),
                    updateDTO.getAppointmentDate(),
                    newEndTime
            );

            if (!isAvailable) {
                // Vérifier si c'est le même créneau (même date/heure)
                if (!updateDTO.getAppointmentDate().equals(visit.getAppointmentDate())) {
                    throw new IllegalArgumentException("Ce créneau n'est pas disponible pour l'agent sélectionné");
                }
            }
        }

        // Mettre à jour les champs
        if (updateDTO.getAppointmentDate() != null) {
            visit.setAppointmentDate(updateDTO.getAppointmentDate());
        }
        if (updateDTO.getDurationMinutes() != null) {
            visit.setDurationMinutes(updateDTO.getDurationMinutes());
        }
        if (updateDTO.getVisitorName() != null) {
            visit.setVisitorName(updateDTO.getVisitorName());
        }
        if (updateDTO.getVisitorEmail() != null) {
            visit.setVisitorEmail(updateDTO.getVisitorEmail());
        }
        if (updateDTO.getVisitorPhone() != null) {
            visit.setVisitorPhone(updateDTO.getVisitorPhone());
        }
        if (updateDTO.getNotes() != null) {
            visit.setNotes(updateDTO.getNotes());
        }
        if (updateDTO.getAgentNotes() != null) {
            visit.setAgentNotes(updateDTO.getAgentNotes());
        }
        if (updateDTO.getStatus() != null) {
            visit.setStatus(updateDTO.getStatus());
            if ("CONFIRMED".equals(updateDTO.getStatus()) && visit.getConfirmedAt() == null) {
                visit.setConfirmedAt(LocalDateTime.now());
            }
            if ("CANCELLED".equals(updateDTO.getStatus()) && visit.getCancelledAt() == null) {
                visit.setCancelledAt(LocalDateTime.now());
                if (updateDTO.getCancellationReason() != null) {
                    visit.setCancellationReason(updateDTO.getCancellationReason());
                }
            }
        }
        if (updateDTO.getCancellationReason() != null) {
            visit.setCancellationReason(updateDTO.getCancellationReason());
        }

        VisitAppointment saved = visitAppointmentRepository.save(visit);
        logger.info("Visit updated: ID {}", visitId);

        return enrichDTO(visitAppointmentMapper.toDTO(saved));
    }

    /**
     * Proposer un échange de créneau pour un rendez-vous
     */
    @Transactional
    public VisitAppointmentDTO proposeExchange(Long visitId, LocalDateTime proposedDate, String message) {
        logger.debug("Proposing exchange for visit ID: {} to date: {}", visitId, proposedDate);
        
        VisitAppointment visit = visitAppointmentRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found with ID: " + visitId));

        // Vérifier que le nouveau créneau est disponible
        LocalDateTime endTime = proposedDate.plusMinutes(visit.getDurationMinutes());
        boolean isAvailable = visitAppointmentRepository.isTimeSlotAvailable(
                visit.getAgentId(),
                proposedDate,
                endTime
        );

        if (!isAvailable) {
            throw new IllegalArgumentException("Le créneau proposé n'est pas disponible pour l'agent");
        }

        // Mettre à jour la date proposée et ajouter un message dans les notes
        String exchangeMessage = String.format(
                "[Échange proposé] Nouvelle date proposée: %s. %s",
                proposedDate.toString(),
                message != null ? message : ""
        );
        
        String currentNotes = visit.getNotes() != null ? visit.getNotes() : "";
        visit.setNotes(currentNotes + "\n\n" + exchangeMessage);
        visit.setAppointmentDate(proposedDate);
        
        // Optionnel: Changer le statut en PENDING pour que le visiteur confirme
        if ("CONFIRMED".equals(visit.getStatus())) {
            visit.setStatus("PENDING");
        }

        VisitAppointment saved = visitAppointmentRepository.save(visit);
        logger.info("Exchange proposed for visit ID: {} to date: {}", visitId, proposedDate);

        return enrichDTO(visitAppointmentMapper.toDTO(saved));
    }

    /**
     * Supprimer un rendez-vous (soft delete)
     */
    @Transactional
    public void deleteVisit(Long visitId) {
        logger.debug("Deleting visit ID: {}", visitId);
        VisitAppointment visit = visitAppointmentRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found with ID: " + visitId));
        visit.setActive(false);
        visitAppointmentRepository.save(visit);
        logger.info("Visit deleted: ID {}", visitId);
    }

    /**
     * Enrichir le DTO avec les informations de la propriété et de l'agent
     */
    private VisitAppointmentDTO enrichDTO(VisitAppointmentDTO dto) {
        if (dto == null) {
            return dto;
        }
        
        if (dto.getPropertyId() != null) {
            propertyRepository.findById(dto.getPropertyId()).ifPresent(property -> {
                dto.setPropertyTitle(property.getTitle());
            });
        }
        
        // TODO: Enrichir avec les informations de l'agent depuis identity-service
        // Pour l'instant, on laisse agentName null
        
        return dto;
    }
}

