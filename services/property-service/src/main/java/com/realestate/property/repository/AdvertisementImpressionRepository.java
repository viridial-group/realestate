package com.realestate.property.repository;

import com.realestate.property.entity.AdvertisementImpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementImpressionRepository extends JpaRepository<AdvertisementImpression, Long> {

    // Compter les impressions pour une annonce
    Long countByAdvertisementId(Long advertisementId);

    // Compter les impressions pour une annonce dans une période
    @Query("SELECT COUNT(i) FROM AdvertisementImpression i WHERE i.advertisementId = :advertisementId " +
           "AND i.impressedAt BETWEEN :startDate AND :endDate")
    Long countByAdvertisementIdAndDateRange(
        @Param("advertisementId") Long advertisementId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // Compter les impressions par jour pour une annonce
    @Query(value = "SELECT COUNT(*) FROM advertisement_impressions WHERE advertisement_id = :advertisementId " +
           "AND DATE(impressed_at) = DATE(:date)", nativeQuery = true)
    Long countByAdvertisementIdAndDate(
        @Param("advertisementId") Long advertisementId,
        @Param("date") LocalDateTime date
    );

    // Trouver les impressions pour une annonce
    List<AdvertisementImpression> findByAdvertisementIdOrderByImpressedAtDesc(Long advertisementId);
}

