package com.realestate.audit.service;

import com.realestate.common.document.AuditLogDocument;
import com.realestate.common.repository.elasticsearch.AuditLogDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de recherche Elasticsearch pour les Audit Logs
 */
@Service
public class AuditSearchService {

    private final AuditLogDocumentRepository auditLogDocumentRepository;

    public AuditSearchService(AuditLogDocumentRepository auditLogDocumentRepository) {
        this.auditLogDocumentRepository = auditLogDocumentRepository;
    }

    /**
     * Recherche par organisation
     */
    public Page<AuditLogDocument> searchByOrganization(Long organizationId, Pageable pageable) {
        return auditLogDocumentRepository.findByOrganizationId(organizationId, pageable);
    }

    /**
     * Recherche par acteur (user ID)
     */
    public Page<AuditLogDocument> searchByActor(Long actorId, Pageable pageable) {
        return auditLogDocumentRepository.findByActorId(actorId, pageable);
    }

    /**
     * Recherche par action
     */
    public List<AuditLogDocument> searchByAction(String action) {
        return auditLogDocumentRepository.findByAction(action);
    }

    /**
     * Recherche par type de cible
     */
    public List<AuditLogDocument> searchByTargetType(String targetType) {
        return auditLogDocumentRepository.findByTargetType(targetType);
    }

    /**
     * Recherche par organisation et action
     */
    public Page<AuditLogDocument> searchByOrganizationAndAction(
            Long organizationId, String action, Pageable pageable) {
        return auditLogDocumentRepository.findByOrganizationIdAndAction(organizationId, action, pageable);
    }

    /**
     * Recherche par organisation et type de cible
     */
    public Page<AuditLogDocument> searchByOrganizationAndTargetType(
            Long organizationId, String targetType, Pageable pageable) {
        return auditLogDocumentRepository.findByOrganizationIdAndTargetType(organizationId, targetType, pageable);
    }

    /**
     * Recherche par plage de dates
     */
    public Page<AuditLogDocument> searchByDateRange(
            LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return auditLogDocumentRepository.findByTimestampBetween(start, end, pageable);
    }

    /**
     * Recherche par organisation et plage de dates
     */
    public Page<AuditLogDocument> searchByOrganizationAndDateRange(
            Long organizationId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return auditLogDocumentRepository.findByOrganizationIdAndTimestampBetween(
                organizationId, start, end, pageable);
    }

    /**
     * Recherche full-text dans description
     */
    public Page<AuditLogDocument> searchByText(String searchTerm, Pageable pageable) {
        return auditLogDocumentRepository.findByDescriptionContaining(searchTerm, pageable);
    }
}

