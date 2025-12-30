package com.realestate.audit.service;

import com.realestate.audit.entity.AuditLog;
import com.realestate.audit.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog createAuditLog(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    @Transactional
    public AuditLog logAction(
            Long actorId,
            String actorEmail,
            Long organizationId,
            String action,
            String targetType,
            Long targetId,
            String status,
            String description,
            String ipAddress,
            String userAgent,
            String requestMethod,
            String requestPath,
            String metadata) {
        AuditLog auditLog = new AuditLog(actorId, organizationId, action);
        auditLog.setActorEmail(actorEmail);
        auditLog.setTargetType(targetType);
        auditLog.setTargetId(targetId);
        auditLog.setStatus(status);
        auditLog.setDescription(description);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);
        auditLog.setRequestMethod(requestMethod);
        auditLog.setRequestPath(requestPath);
        auditLog.setMetadata(metadata);

        return auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public Optional<AuditLog> getAuditLogById(Long id) {
        return auditLogRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByOrganizationId(Long organizationId, Pageable pageable) {
        return auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByActorIdAndOrganizationId(Long actorId, Long organizationId, Pageable pageable) {
        return auditLogRepository.findByActorIdAndOrganizationIdOrderByCreatedAtDesc(actorId, organizationId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByActionAndOrganizationId(String action, Long organizationId, Pageable pageable) {
        return auditLogRepository.findByActionAndOrganizationIdOrderByCreatedAtDesc(action, organizationId, pageable);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByTarget(String targetType, Long targetId) {
        return auditLogRepository.findByTargetOrderByCreatedAtDesc(targetType, targetId);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByDateRange(
            Long organizationId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        return auditLogRepository.findByOrganizationIdAndDateRange(organizationId, startDate, endDate, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByStatusAndOrganizationId(String status, Long organizationId, Pageable pageable) {
        return auditLogRepository.findByOrganizationIdAndStatusOrderByCreatedAtDesc(organizationId, status, pageable);
    }
}

