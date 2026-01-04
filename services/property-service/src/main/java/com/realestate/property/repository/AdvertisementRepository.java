package com.realestate.property.repository;

import com.realestate.property.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    // Trouver les annonces actives pour une organisation
    List<Advertisement> findByOrganizationIdAndActiveTrueAndStatusOrderByPriorityDesc(Long organizationId, String status);

    // Trouver les annonces actives par type et position
    @Query("SELECT a FROM Advertisement a WHERE a.active = true AND a.status = 'ACTIVE' " +
           "AND (a.startDate IS NULL OR a.startDate <= :now) " +
           "AND (a.endDate IS NULL OR a.endDate >= :now) " +
           "AND a.adType = :adType AND a.position = :position " +
           "ORDER BY a.priority DESC")
    List<Advertisement> findActiveByTypeAndPosition(
        @Param("adType") String adType,
        @Param("position") String position,
        @Param("now") LocalDateTime now
    );

    // Trouver les annonces actives pour une localisation
    @Query("SELECT a FROM Advertisement a WHERE a.active = true AND a.status = 'ACTIVE' " +
           "AND (a.startDate IS NULL OR a.startDate <= :now) " +
           "AND (a.endDate IS NULL OR a.endDate >= :now) " +
           "AND (a.targetLocations IS NULL OR a.targetLocations = '' OR " +
           "     a.targetLocations LIKE %:city% OR a.targetLocations LIKE %:postalCode%) " +
           "ORDER BY a.priority DESC")
    List<Advertisement> findActiveByLocation(
        @Param("city") String city,
        @Param("postalCode") String postalCode,
        @Param("now") LocalDateTime now
    );

    // Trouver les annonces actives pour un type de propriété
    @Query("SELECT a FROM Advertisement a WHERE a.active = true AND a.status = 'ACTIVE' " +
           "AND (a.startDate IS NULL OR a.startDate <= :now) " +
           "AND (a.endDate IS NULL OR a.endDate >= :now) " +
           "AND (a.targetPropertyTypes IS NULL OR a.targetPropertyTypes = '' OR " +
           "     a.targetPropertyTypes LIKE %:propertyType%) " +
           "ORDER BY a.priority DESC")
    List<Advertisement> findActiveByPropertyType(
        @Param("propertyType") String propertyType,
        @Param("now") LocalDateTime now
    );

    // Statistiques par organisation
    @Query("SELECT COUNT(a) FROM Advertisement a WHERE a.organizationId = :organizationId")
    Long countByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT COUNT(a) FROM Advertisement a WHERE a.organizationId = :organizationId AND a.status = 'ACTIVE'")
    Long countActiveByOrganizationId(@Param("organizationId") Long organizationId);

    // Trouver les annonces expirées
    @Query("SELECT a FROM Advertisement a WHERE a.status = 'ACTIVE' AND a.endDate IS NOT NULL AND a.endDate < :now")
    List<Advertisement> findExpired(@Param("now") LocalDateTime now);

    // Trouver toutes les annonces d'une organisation
    List<Advertisement> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId);

    // Trouver les annonces par statut
    List<Advertisement> findByOrganizationIdAndStatusOrderByCreatedAtDesc(Long organizationId, String status);
}

