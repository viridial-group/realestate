package com.realestate.identity.repository;

import com.realestate.identity.entity.OrganizationContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationContactMessageRepository extends JpaRepository<OrganizationContactMessage, Long> {

    @Query("SELECT cm FROM OrganizationContactMessage cm WHERE cm.active = true ORDER BY cm.createdAt DESC")
    Page<OrganizationContactMessage> findAllActive(Pageable pageable);

    @Query("SELECT cm FROM OrganizationContactMessage cm WHERE cm.organizationId = :organizationId AND cm.active = true ORDER BY cm.createdAt DESC")
    Page<OrganizationContactMessage> findByOrganizationId(@Param("organizationId") Long organizationId, Pageable pageable);

    @Query("SELECT cm FROM OrganizationContactMessage cm WHERE cm.status = :status AND cm.active = true ORDER BY cm.createdAt DESC")
    Page<OrganizationContactMessage> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT cm FROM OrganizationContactMessage cm WHERE cm.organizationId = :organizationId AND cm.status = :status AND cm.active = true ORDER BY cm.createdAt DESC")
    Page<OrganizationContactMessage> findByOrganizationIdAndStatus(
            @Param("organizationId") Long organizationId,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT COUNT(cm) FROM OrganizationContactMessage cm WHERE cm.status = 'NEW' AND cm.active = true")
    long countNewMessages();

    @Query("SELECT COUNT(cm) FROM OrganizationContactMessage cm WHERE cm.organizationId = :organizationId AND cm.status = 'NEW' AND cm.active = true")
    long countNewMessagesByOrganizationId(@Param("organizationId") Long organizationId);
}

