package com.realestate.emailing.repository;

import com.realestate.emailing.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    List<Email> findByRecipientEmail(String recipientEmail);

    List<Email> findByOrganizationId(Long organizationId);

    @Query("SELECT e FROM Email e WHERE e.status = :status AND e.active = true ORDER BY e.createdAt DESC")
    List<Email> findActiveByStatus(@Param("status") String status);

    @Query("SELECT e FROM Email e WHERE e.recipientId = :recipientId AND e.active = true ORDER BY e.createdAt DESC")
    List<Email> findActiveByRecipientId(@Param("recipientId") Long recipientId);

    @Query("SELECT e FROM Email e WHERE e.organizationId = :organizationId AND e.status = :status AND e.active = true ORDER BY e.createdAt DESC")
    List<Email> findActiveByOrganizationIdAndStatus(
            @Param("organizationId") Long organizationId,
            @Param("status") String status
    );

    @Query("SELECT e FROM Email e WHERE e.status = 'PENDING' AND e.active = true AND e.retryCount < :maxRetries ORDER BY e.createdAt ASC")
    List<Email> findPendingEmailsForRetry(@Param("maxRetries") Integer maxRetries);

    @Query("SELECT e FROM Email e WHERE e.status = 'FAILED' AND e.active = true AND e.retryCount < :maxRetries AND e.failedAt < :beforeDate ORDER BY e.failedAt ASC")
    List<Email> findFailedEmailsForRetry(
            @Param("maxRetries") Integer maxRetries,
            @Param("beforeDate") LocalDateTime beforeDate
    );
}

