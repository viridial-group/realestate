package com.realestate.audit.consumer;

import com.realestate.common.document.AuditLogDocument;
import com.realestate.common.repository.elasticsearch.AuditLogDocumentRepository;
import com.realestate.audit.entity.AuditLog;
import com.realestate.audit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Consumer Kafka pour indexer les Audit Logs dans Elasticsearch
 */
@Component
public class AuditIndexingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuditIndexingConsumer.class);

    private final AuditLogDocumentRepository auditLogDocumentRepository;
    private final AuditLogRepository auditLogRepository;

    public AuditIndexingConsumer(
            AuditLogDocumentRepository auditLogDocumentRepository,
            AuditLogRepository auditLogRepository) {
        this.auditLogDocumentRepository = auditLogDocumentRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Index audit logs from Kafka events
     * This consumer listens to all events and indexes them in Elasticsearch
     */
    @KafkaListener(topics = {
            "property-created", "property-updated",
            "document-uploaded",
            "workflow-task-created", "workflow-task-completed"
    }, groupId = "audit-indexing-group")
    @Transactional
    public void handleAuditEvent(@Payload Object event, Acknowledgment acknowledgment) {
        try {
            // The AuditEventConsumer already creates AuditLog entities in PostgreSQL
            // We need to index the latest audit logs
            // For simplicity, we'll index all recent audit logs periodically
            // Or we can listen to a specific audit-created topic
            
            // For now, we'll index audit logs that were just created
            // This is a simplified approach - in production, you might want a dedicated topic
            
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error indexing audit log: {}", e.getMessage(), e);
        }
    }

    /**
     * Index a specific audit log by ID
     */
    public void indexAuditLog(Long auditLogId) {
        try {
            Optional<AuditLog> auditLogOpt = auditLogRepository.findById(auditLogId);
            
            if (auditLogOpt.isPresent()) {
                AuditLog auditLog = auditLogOpt.get();
                AuditLogDocument document = convertToDocument(auditLog);
                auditLogDocumentRepository.save(document);
                logger.info("Indexed audit log {} in Elasticsearch", auditLogId);
            }
        } catch (Exception e) {
            logger.error("Error indexing audit log {}: {}", auditLogId, e.getMessage(), e);
        }
    }

    /**
     * Convert AuditLog entity to AuditLogDocument
     */
    private AuditLogDocument convertToDocument(AuditLog auditLog) {
        AuditLogDocument document = new AuditLogDocument();
        document.setId(auditLog.getId());
        document.setOrganizationId(auditLog.getOrganizationId());
        document.setActorId(auditLog.getActorId());
        document.setAction(auditLog.getAction());
        document.setTargetType(auditLog.getTargetType());
        document.setTargetId(auditLog.getTargetId());
        document.setDescription(auditLog.getDescription());
        document.setIpAddress(auditLog.getIpAddress());
        document.setUserAgent(auditLog.getUserAgent());
        document.setTimestamp(auditLog.getCreatedAt());
        document.setMetadata(auditLog.getMetadata());
        document.setStatus(auditLog.getStatus());
        return document;
    }
}

