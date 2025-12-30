package com.realestate.property.repository;

import com.realestate.property.entity.PropertyAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyAccessRepository extends JpaRepository<PropertyAccess, Long> {

    Optional<PropertyAccess> findByPropertyIdAndUserIdAndOrganizationId(
            Long propertyId, Long userId, Long organizationId
    );

    Optional<PropertyAccess> findByPropertyIdAndOrganizationId(
            Long propertyId, Long organizationId
    );

    List<PropertyAccess> findByPropertyId(Long propertyId);

    List<PropertyAccess> findByOrganizationId(Long organizationId);

    List<PropertyAccess> findByUserId(Long userId);

    @Query("SELECT pa FROM PropertyAccess pa WHERE pa.property.id = :propertyId AND pa.organizationId = :organizationId AND pa.active = true AND (pa.expiresAt IS NULL OR pa.expiresAt > :now)")
    Optional<PropertyAccess> findActiveByPropertyIdAndOrganizationId(
            @Param("propertyId") Long propertyId,
            @Param("organizationId") Long organizationId,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT pa FROM PropertyAccess pa WHERE pa.organizationId = :organizationId AND pa.active = true AND (pa.expiresAt IS NULL OR pa.expiresAt > :now)")
    List<PropertyAccess> findActiveByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("now") LocalDateTime now
    );
}

