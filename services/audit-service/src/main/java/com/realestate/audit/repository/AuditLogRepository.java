package com.realestate.audit.repository;

import com.realestate.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByActorId(Long actorId);

    List<AuditLog> findByOrganizationId(Long organizationId);

    @Query("SELECT a FROM AuditLog a WHERE a.organizationId = :organizationId ORDER BY a.createdAt DESC")
    Page<AuditLog> findByOrganizationIdOrderByCreatedAtDesc(
            @Param("organizationId") Long organizationId,
            Pageable pageable
    );

    @Query("SELECT a FROM AuditLog a WHERE a.actorId = :actorId AND a.organizationId = :organizationId ORDER BY a.createdAt DESC")
    Page<AuditLog> findByActorIdAndOrganizationIdOrderByCreatedAtDesc(
            @Param("actorId") Long actorId,
            @Param("organizationId") Long organizationId,
            Pageable pageable
    );

    @Query("SELECT a FROM AuditLog a WHERE a.action = :action AND a.organizationId = :organizationId ORDER BY a.createdAt DESC")
    Page<AuditLog> findByActionAndOrganizationIdOrderByCreatedAtDesc(
            @Param("action") String action,
            @Param("organizationId") Long organizationId,
            Pageable pageable
    );

    @Query("SELECT a FROM AuditLog a WHERE a.targetType = :targetType AND a.targetId = :targetId ORDER BY a.createdAt DESC")
    List<AuditLog> findByTargetOrderByCreatedAtDesc(
            @Param("targetType") String targetType,
            @Param("targetId") Long targetId
    );

    @Query("SELECT a FROM AuditLog a WHERE a.organizationId = :organizationId AND a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    Page<AuditLog> findByOrganizationIdAndDateRange(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT a FROM AuditLog a WHERE a.organizationId = :organizationId AND a.status = :status ORDER BY a.createdAt DESC")
    Page<AuditLog> findByOrganizationIdAndStatusOrderByCreatedAtDesc(
            @Param("organizationId") Long organizationId,
            @Param("status") String status,
            Pageable pageable
    );
}

