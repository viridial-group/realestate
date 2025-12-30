package com.realestate.notification.repository;

import com.realestate.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientId(Long recipientId);

    List<Notification> findByOrganizationId(Long organizationId);

    @Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId AND n.active = true ORDER BY n.createdAt DESC")
    List<Notification> findActiveByRecipientIdOrderByCreatedAtDesc(@Param("recipientId") Long recipientId);

    @Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId AND n.status = :status AND n.active = true ORDER BY n.createdAt DESC")
    List<Notification> findActiveByRecipientIdAndStatus(
            @Param("recipientId") Long recipientId,
            @Param("status") String status
    );

    @Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId AND n.status = 'PENDING' AND n.active = true ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByRecipientId(@Param("recipientId") Long recipientId);

    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.active = true ORDER BY n.createdAt DESC")
    List<Notification> findActiveByType(@Param("type") String type);

    @Query("SELECT n FROM Notification n WHERE n.organizationId = :organizationId AND n.type = :type AND n.active = true ORDER BY n.createdAt DESC")
    List<Notification> findActiveByOrganizationIdAndType(
            @Param("organizationId") Long organizationId,
            @Param("type") String type
    );

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipientId = :recipientId AND n.status = 'PENDING' AND n.active = true")
    Long countUnreadByRecipientId(@Param("recipientId") Long recipientId);
}

