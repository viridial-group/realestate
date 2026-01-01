package com.realestate.property.repository;

import com.realestate.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    Optional<Property> findByReference(String reference);

    List<Property> findByOrganizationId(Long organizationId);

    @Query("SELECT p FROM Property p WHERE p.organizationId = :organizationId AND p.active = true")
    List<Property> findActiveByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT p FROM Property p WHERE p.assignedUserId = :userId AND p.active = true")
    List<Property> findActiveByAssignedUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Property p WHERE p.teamId = :teamId AND p.active = true")
    List<Property> findActiveByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT p FROM Property p WHERE p.status = :status AND p.active = true")
    List<Property> findActiveByStatus(@Param("status") String status);

    @Query("SELECT p FROM Property p WHERE p.type = :type AND p.active = true")
    List<Property> findActiveByType(@Param("type") String type);

    @Query("SELECT p FROM Property p WHERE p.city = :city AND p.active = true")
    List<Property> findActiveByCity(@Param("city") String city);

    @Query("SELECT p FROM Property p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = true")
    List<Property> findActiveByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT p FROM Property p WHERE p.surface BETWEEN :minSurface AND :maxSurface AND p.active = true")
    List<Property> findActiveBySurfaceRange(
            @Param("minSurface") BigDecimal minSurface,
            @Param("maxSurface") BigDecimal maxSurface
    );

    @Query("SELECT p FROM Property p WHERE p.organizationId = :organizationId AND p.status = :status AND p.active = true")
    List<Property> findActiveByOrganizationIdAndStatus(
            @Param("organizationId") Long organizationId,
            @Param("status") String status
    );
}

