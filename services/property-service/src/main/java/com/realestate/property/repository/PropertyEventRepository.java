package com.realestate.property.repository;

import com.realestate.property.entity.PropertyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour les événements de propriétés
 */
@Repository
public interface PropertyEventRepository extends JpaRepository<PropertyEvent, Long> {

    /**
     * Récupère tous les événements pour une propriété
     */
    List<PropertyEvent> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    /**
     * Récupère les événements pour une propriété dans une période
     */
    @Query("SELECT e FROM PropertyEvent e WHERE e.propertyId = :propertyId " +
           "AND DATE(e.createdAt) >= :startDate AND DATE(e.createdAt) <= :endDate " +
           "ORDER BY e.createdAt ASC")
    List<PropertyEvent> findByPropertyIdAndDateRange(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Récupère tous les événements dans une période (global)
     */
    @Query("SELECT e FROM PropertyEvent e WHERE " +
           "DATE(e.createdAt) >= :startDate AND DATE(e.createdAt) <= :endDate " +
           "ORDER BY e.createdAt ASC")
    List<PropertyEvent> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Compte les événements par type pour une propriété
     */
    @Query("SELECT e.eventType, COUNT(e) FROM PropertyEvent e WHERE e.propertyId = :propertyId GROUP BY e.eventType")
    List<Object[]> countByEventTypeForProperty(@Param("propertyId") Long propertyId);

    /**
     * Compte les événements par type (global)
     */
    @Query("SELECT e.eventType, COUNT(e) FROM PropertyEvent e GROUP BY e.eventType")
    List<Object[]> countByEventType();
}

