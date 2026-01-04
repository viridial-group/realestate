package com.realestate.property.repository;

import com.realestate.property.entity.AdvertisementClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementClickRepository extends JpaRepository<AdvertisementClick, Long> {

    // Compter les clics pour une annonce
    Long countByAdvertisementId(Long advertisementId);

    // Compter les clics pour une annonce dans une période
    @Query("SELECT COUNT(c) FROM AdvertisementClick c WHERE c.advertisementId = :advertisementId " +
           "AND c.clickedAt BETWEEN :startDate AND :endDate")
    Long countByAdvertisementIdAndDateRange(
        @Param("advertisementId") Long advertisementId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // Trouver les clics pour une annonce
    List<AdvertisementClick> findByAdvertisementIdOrderByClickedAtDesc(Long advertisementId);
}

