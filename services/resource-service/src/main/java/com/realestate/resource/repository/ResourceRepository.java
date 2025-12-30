package com.realestate.resource.repository;

import com.realestate.resource.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByDomainId(Long domainId);

    List<Resource> findByOrganizationId(Long organizationId);

    @Query("SELECT r FROM Resource r WHERE r.organizationId = :organizationId AND r.active = true")
    List<Resource> findActiveByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT r FROM Resource r WHERE r.shared = true AND r.active = true")
    List<Resource> findAllShared();

    @Query("SELECT r FROM Resource r WHERE r.domain.id = :domainId AND r.organizationId = :organizationId AND r.active = true")
    List<Resource> findByDomainIdAndOrganizationId(
            @Param("domainId") Long domainId,
            @Param("organizationId") Long organizationId
    );

    @Query("SELECT r FROM Resource r JOIN FETCH r.tags WHERE r.id = :id")
    Optional<Resource> findByIdWithTags(@Param("id") Long id);

    @Query("SELECT r FROM Resource r JOIN FETCH r.accesses WHERE r.id = :id")
    Optional<Resource> findByIdWithAccesses(@Param("id") Long id);
}

