package com.realestate.resource.repository;

import com.realestate.resource.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByNameContainingIgnoreCase(String name);

    List<Tag> findByDomainId(Long domainId);

    List<Tag> findByOrganizationId(Long organizationId);

    @Query("SELECT t FROM Tag t WHERE t.domain.id = :domainId AND t.active = true")
    List<Tag> findActiveByDomainId(@Param("domainId") Long domainId);

    @Query("SELECT t FROM Tag t WHERE t.organizationId = :organizationId AND t.active = true")
    List<Tag> findActiveByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT t FROM Tag t JOIN FETCH t.resources WHERE t.id = :id")
    Optional<Tag> findByIdWithResources(@Param("id") Long id);
}

