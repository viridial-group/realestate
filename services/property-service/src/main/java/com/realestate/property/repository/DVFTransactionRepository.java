package com.realestate.property.repository;

import com.realestate.property.entity.DVFTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DVFTransactionRepository extends JpaRepository<DVFTransaction, Long> {

    /**
     * Trouve les transactions dans une zone géographique (rayon en km)
     */
    @Query(value = """
        SELECT * FROM dvf_transactions 
        WHERE latitude IS NOT NULL 
        AND longitude IS NOT NULL
        AND type_local = :typeLocal
        AND mutation_date >= :startDate
        AND mutation_date <= :endDate
        AND (
            6371 * acos(
                cos(radians(:latitude)) * 
                cos(radians(latitude)) * 
                cos(radians(longitude) - radians(:longitude)) + 
                sin(radians(:latitude)) * 
                sin(radians(latitude))
            )
        ) <= :radiusKm
        ORDER BY mutation_date DESC
        """, nativeQuery = true)
    List<DVFTransaction> findNearbyTransactions(
        @Param("latitude") BigDecimal latitude,
        @Param("longitude") BigDecimal longitude,
        @Param("radiusKm") double radiusKm,
        @Param("typeLocal") String typeLocal,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );

    /**
     * Trouve les transactions par code postal
     */
    List<DVFTransaction> findByCodePostalAndTypeLocalAndMutationDateBetween(
        String codePostal,
        String typeLocal,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * Trouve les transactions par commune
     */
    List<DVFTransaction> findByCommuneCodeAndTypeLocalAndMutationDateBetween(
        String communeCode,
        String typeLocal,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * Calcule le prix moyen au m² pour une zone
     */
    @Query("""
        SELECT AVG(t.prixMetreCarre) 
        FROM DVFTransaction t 
        WHERE t.codePostal = :codePostal 
        AND t.typeLocal = :typeLocal 
        AND t.mutationDate >= :startDate 
        AND t.mutationDate <= :endDate
        AND t.prixMetreCarre IS NOT NULL
        """)
    BigDecimal calculateAveragePricePerSquareMeter(
        @Param("codePostal") String codePostal,
        @Param("typeLocal") String typeLocal,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Calcule le prix médian au m² pour une zone
     */
    @Query(value = """
        SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY prix_metre_carre) 
        FROM dvf_transactions 
        WHERE code_postal = :codePostal 
        AND type_local = :typeLocal 
        AND mutation_date >= :startDate 
        AND mutation_date <= :endDate
        AND prix_metre_carre IS NOT NULL
        """, nativeQuery = true)
    BigDecimal calculateMedianPricePerSquareMeter(
        @Param("codePostal") String codePostal,
        @Param("typeLocal") String typeLocal,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Compte les transactions dans une zone
     */
    Long countByCodePostalAndTypeLocalAndMutationDateBetween(
        String codePostal,
        String typeLocal,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * Trouve les transactions similaires (même type, surface proche)
     */
    @Query("""
        SELECT t FROM DVFTransaction t 
        WHERE t.codePostal = :codePostal 
        AND t.typeLocal = :typeLocal 
        AND t.mutationDate >= :startDate 
        AND t.mutationDate <= :endDate
        AND t.surfaceReelleBati BETWEEN :minSurface AND :maxSurface
        AND t.prixMetreCarre IS NOT NULL
        ORDER BY ABS(t.surfaceReelleBati - :targetSurface) ASC, t.mutationDate DESC
        """)
    List<DVFTransaction> findSimilarTransactions(
        @Param("codePostal") String codePostal,
        @Param("typeLocal") String typeLocal,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("targetSurface") BigDecimal targetSurface,
        @Param("minSurface") BigDecimal minSurface,
        @Param("maxSurface") BigDecimal maxSurface,
        Pageable pageable
    );
}

