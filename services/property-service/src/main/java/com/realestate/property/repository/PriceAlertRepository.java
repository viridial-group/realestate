package com.realestate.property.repository;

import com.realestate.property.entity.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {

    /**
     * Récupérer toutes les alertes actives pour une propriété
     */
    List<PriceAlert> findByPropertyIdAndActiveTrue(Long propertyId);

    /**
     * Récupérer toutes les alertes actives pour un utilisateur
     */
    List<PriceAlert> findByUserIdAndActiveTrue(Long userId);

    /**
     * Récupérer une alerte spécifique pour une propriété et un utilisateur
     */
    Optional<PriceAlert> findByPropertyIdAndUserIdAndActiveTrue(Long propertyId, Long userId);

    /**
     * Récupérer toutes les alertes actives qui n'ont pas encore été notifiées
     */
    @Query("SELECT pa FROM PriceAlert pa WHERE pa.active = true AND pa.notified = false")
    List<PriceAlert> findActiveUnnotifiedAlerts();

    /**
     * Récupérer toutes les alertes actives pour une propriété qui n'ont pas encore été notifiées
     */
    @Query("SELECT pa FROM PriceAlert pa WHERE pa.propertyId = :propertyId AND pa.active = true AND pa.notified = false")
    List<PriceAlert> findActiveUnnotifiedAlertsByPropertyId(@Param("propertyId") Long propertyId);

    /**
     * Compter le nombre d'alertes actives pour un utilisateur
     */
    long countByUserIdAndActiveTrue(Long userId);
}

