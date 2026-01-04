package com.realestate.property.repository;

import com.realestate.property.entity.PriceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    /**
     * Récupérer l'historique des prix pour une propriété, trié par date décroissante
     */
    List<PriceHistory> findByPropertyIdOrderByChangeDateDesc(Long propertyId);

    /**
     * Récupérer l'historique des prix pour une propriété avec pagination
     */
    Page<PriceHistory> findByPropertyIdOrderByChangeDateDesc(Long propertyId, Pageable pageable);

    /**
     * Récupérer l'historique des prix pour une propriété dans une période donnée
     */
    @Query("SELECT ph FROM PriceHistory ph WHERE ph.propertyId = :propertyId " +
           "AND ph.changeDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ph.changeDate DESC")
    List<PriceHistory> findByPropertyIdAndDateRange(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Récupérer le prix le plus ancien pour une propriété
     */
    Optional<PriceHistory> findFirstByPropertyIdOrderByChangeDateAsc(Long propertyId);

    /**
     * Récupérer le prix le plus récent pour une propriété
     */
    Optional<PriceHistory> findFirstByPropertyIdOrderByChangeDateDesc(Long propertyId);

    /**
     * Compter le nombre de changements de prix pour une propriété
     */
    long countByPropertyId(Long propertyId);

    /**
     * Récupérer le prix minimum pour une propriété
     */
    @Query("SELECT MIN(ph.price) FROM PriceHistory ph WHERE ph.propertyId = :propertyId")
    Optional<BigDecimal> findMinPriceByPropertyId(@Param("propertyId") Long propertyId);

    /**
     * Récupérer le prix maximum pour une propriété
     */
    @Query("SELECT MAX(ph.price) FROM PriceHistory ph WHERE ph.propertyId = :propertyId")
    Optional<BigDecimal> findMaxPriceByPropertyId(@Param("propertyId") Long propertyId);
}

