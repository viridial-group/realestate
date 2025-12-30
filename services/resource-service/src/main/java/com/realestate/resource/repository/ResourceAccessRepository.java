package com.realestate.resource.repository;

import com.realestate.resource.entity.ResourceAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceAccessRepository extends JpaRepository<ResourceAccess, Long> {

    Optional<ResourceAccess> findByResourceIdAndOrganizationId(Long resourceId, Long organizationId);

    List<ResourceAccess> findByResourceId(Long resourceId);

    List<ResourceAccess> findByOrganizationId(Long organizationId);

    @Query("SELECT ra FROM ResourceAccess ra WHERE ra.resource.id = :resourceId AND ra.organizationId = :organizationId AND ra.active = true")
    Optional<ResourceAccess> findActiveByResourceIdAndOrganizationId(
            @Param("resourceId") Long resourceId,
            @Param("organizationId") Long organizationId
    );

    @Query("SELECT ra FROM ResourceAccess ra WHERE ra.organizationId = :organizationId AND ra.active = true")
    List<ResourceAccess> findActiveByOrganizationId(@Param("organizationId") Long organizationId);
}

