package com.realestate.property.repository;

import com.realestate.property.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    @Query("SELECT cm FROM ContactMessage cm WHERE cm.active = true ORDER BY cm.createdAt DESC")
    Page<ContactMessage> findAllActive(Pageable pageable);

    @Query("SELECT cm FROM ContactMessage cm WHERE cm.organizationId = :organizationId AND cm.active = true ORDER BY cm.createdAt DESC")
    Page<ContactMessage> findByOrganizationId(@Param("organizationId") Long organizationId, Pageable pageable);

    @Query("SELECT cm FROM ContactMessage cm WHERE cm.propertyId = :propertyId AND cm.active = true ORDER BY cm.createdAt DESC")
    List<ContactMessage> findByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT cm FROM ContactMessage cm WHERE cm.status = :status AND cm.active = true ORDER BY cm.createdAt DESC")
    Page<ContactMessage> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT cm FROM ContactMessage cm WHERE cm.organizationId = :organizationId AND cm.status = :status AND cm.active = true ORDER BY cm.createdAt DESC")
    Page<ContactMessage> findByOrganizationIdAndStatus(
            @Param("organizationId") Long organizationId,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT COUNT(cm) FROM ContactMessage cm WHERE cm.status = 'NEW' AND cm.active = true")
    long countNewMessages();

    @Query("SELECT COUNT(cm) FROM ContactMessage cm WHERE cm.organizationId = :organizationId AND cm.status = 'NEW' AND cm.active = true")
    long countNewMessagesByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT cm FROM ContactMessage cm " +
           "JOIN Property p ON cm.propertyId = p.id " +
           "WHERE p.assignedUserId = :assignedUserId AND cm.active = true " +
           "ORDER BY cm.createdAt DESC")
    Page<ContactMessage> findByAssignedUserId(@Param("assignedUserId") Long assignedUserId, Pageable pageable);

    @Query("SELECT cm FROM ContactMessage cm " +
           "JOIN Property p ON cm.propertyId = p.id " +
           "WHERE p.assignedUserId = :assignedUserId AND cm.status = :status AND cm.active = true " +
           "ORDER BY cm.createdAt DESC")
    Page<ContactMessage> findByAssignedUserIdAndStatus(
            @Param("assignedUserId") Long assignedUserId,
            @Param("status") String status,
            Pageable pageable);

    @Query("SELECT COUNT(cm) FROM ContactMessage cm " +
           "JOIN Property p ON cm.propertyId = p.id " +
           "WHERE p.assignedUserId = :assignedUserId AND cm.status = 'NEW' AND cm.active = true")
    long countNewMessagesByAssignedUserId(@Param("assignedUserId") Long assignedUserId);

    @Query("SELECT COUNT(cm) FROM ContactMessage cm WHERE cm.propertyId = :propertyId AND cm.status = 'NEW' AND cm.active = true")
    long countUnreadMessagesByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT cm.propertyId, COUNT(cm) FROM ContactMessage cm " +
           "WHERE cm.propertyId IN :propertyIds AND cm.status = 'NEW' AND cm.active = true " +
           "GROUP BY cm.propertyId")
    List<Object[]> countUnreadMessagesByPropertyIds(@Param("propertyIds") List<Long> propertyIds);
}

