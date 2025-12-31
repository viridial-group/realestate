package com.realestate.common.repository.elasticsearch;

import com.realestate.common.document.AuditLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Elasticsearch pour les Audit Logs
 */
@Repository
public interface AuditLogDocumentRepository extends ElasticsearchRepository<AuditLogDocument, Long> {

    /**
     * Recherche par organisation
     */
    Page<AuditLogDocument> findByOrganizationId(Long organizationId, Pageable pageable);

    /**
     * Recherche par acteur (user ID)
     */
    Page<AuditLogDocument> findByActorId(Long actorId, Pageable pageable);

    /**
     * Recherche par action
     */
    List<AuditLogDocument> findByAction(String action);

    /**
     * Recherche par type de cible
     */
    List<AuditLogDocument> findByTargetType(String targetType);

    /**
     * Recherche par organisation et action
     */
    Page<AuditLogDocument> findByOrganizationIdAndAction(
            Long organizationId, String action, Pageable pageable);

    /**
     * Recherche par organisation et type de cible
     */
    Page<AuditLogDocument> findByOrganizationIdAndTargetType(
            Long organizationId, String targetType, Pageable pageable);

    /**
     * Recherche par plage de dates
     */
    Page<AuditLogDocument> findByTimestampBetween(
            LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Recherche par organisation et plage de dates
     */
    Page<AuditLogDocument> findByOrganizationIdAndTimestampBetween(
            Long organizationId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Recherche full-text dans description
     */
    Page<AuditLogDocument> findByDescriptionContaining(String searchTerm, Pageable pageable);
}

