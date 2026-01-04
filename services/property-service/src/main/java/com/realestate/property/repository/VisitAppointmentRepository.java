package com.realestate.property.repository;

import com.realestate.property.entity.VisitAppointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitAppointmentRepository extends JpaRepository<VisitAppointment, Long> {

    /**
     * Récupérer toutes les visites pour une propriété
     */
    List<VisitAppointment> findByPropertyIdAndActiveTrueOrderByAppointmentDateDesc(Long propertyId);

    /**
     * Récupérer toutes les visites pour une propriété avec pagination
     */
    Page<VisitAppointment> findByPropertyIdAndActiveTrueOrderByAppointmentDateDesc(Long propertyId, Pageable pageable);

    /**
     * Récupérer toutes les visites pour un utilisateur
     */
    List<VisitAppointment> findByUserIdAndActiveTrueOrderByAppointmentDateDesc(Long userId);

    /**
     * Récupérer toutes les visites pour un agent
     */
    List<VisitAppointment> findByAgentIdAndActiveTrueOrderByAppointmentDateDesc(Long agentId);

    /**
     * Récupérer toutes les visites pour un agent avec pagination
     */
    Page<VisitAppointment> findByAgentIdAndActiveTrueOrderByAppointmentDateDesc(Long agentId, Pageable pageable);

    /**
     * Récupérer les visites par statut
     */
    List<VisitAppointment> findByStatusAndActiveTrueOrderByAppointmentDateAsc(String status);

    /**
     * Récupérer les visites à venir pour une propriété
     */
    @Query("SELECT va FROM VisitAppointment va WHERE va.propertyId = :propertyId " +
           "AND va.appointmentDate >= :now AND va.active = true " +
           "ORDER BY va.appointmentDate ASC")
    List<VisitAppointment> findUpcomingByPropertyId(
            @Param("propertyId") Long propertyId,
            @Param("now") LocalDateTime now
    );

    /**
     * Récupérer les visites à venir pour un agent
     */
    @Query("SELECT va FROM VisitAppointment va WHERE va.agentId = :agentId " +
           "AND va.appointmentDate >= :now AND va.active = true " +
           "ORDER BY va.appointmentDate ASC")
    List<VisitAppointment> findUpcomingByAgentId(
            @Param("agentId") Long agentId,
            @Param("now") LocalDateTime now
    );

    /**
     * Vérifier si un créneau est disponible pour un agent
     */
    @Query("SELECT COUNT(va) = 0 FROM VisitAppointment va WHERE va.agentId = :agentId " +
           "AND va.status IN ('PENDING', 'CONFIRMED') " +
           "AND va.active = true " +
           "AND va.appointmentDate >= :startTime AND va.appointmentDate < :endTime")
    boolean isTimeSlotAvailable(
            @Param("agentId") Long agentId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Récupérer les visites nécessitant un rappel (24h avant)
     */
    @Query("SELECT va FROM VisitAppointment va WHERE va.status = 'CONFIRMED' " +
           "AND va.reminderSent = false " +
           "AND va.appointmentDate BETWEEN :startTime AND :endTime " +
           "AND va.active = true")
    List<VisitAppointment> findAppointmentsNeedingReminder(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Compter les visites par statut pour une propriété
     */
    long countByPropertyIdAndStatusAndActiveTrue(Long propertyId, String status);
}

