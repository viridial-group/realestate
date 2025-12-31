package com.realestate.audit.consumer;

import com.realestate.audit.entity.AuditLog;
import com.realestate.audit.repository.AuditLogRepository;
import com.realestate.common.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Consumer for all events that need to be audited.
 */
@Component
public class AuditEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuditEventConsumer.class);

    private final AuditLogRepository auditLogRepository;

    public AuditEventConsumer(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @KafkaListener(topics = {"property-created", "property-updated"}, groupId = "audit-service-group")
    @Transactional
    public void handlePropertyEvents(@Payload BaseEvent event, Acknowledgment acknowledgment) {
        try {
            String action = event instanceof PropertyCreatedEvent ? "CREATE" : "UPDATE";
            String resourceName = event instanceof PropertyCreatedEvent 
                    ? ((PropertyCreatedEvent) event).getPropertyTitle()
                    : ((PropertyUpdatedEvent) event).getPropertyTitle();
            Long resourceId = event instanceof PropertyCreatedEvent 
                    ? ((PropertyCreatedEvent) event).getPropertyId()
                    : ((PropertyUpdatedEvent) event).getPropertyId();

            createAuditLog(event.getOrganizationId(), event.getUserId(), action, 
                          "PROPERTY", resourceId, resourceName, event.getEventId());
            
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing property event", e);
        }
    }

    @KafkaListener(topics = "document-uploaded", groupId = "audit-service-group")
    @Transactional
    public void handleDocumentUploaded(@Payload DocumentUploadedEvent event, Acknowledgment acknowledgment) {
        try {
            createAuditLog(event.getOrganizationId(), event.getUserId(), "UPLOAD", 
                          "DOCUMENT", event.getDocumentId(), event.getDocumentName(), event.getEventId());
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing DocumentUploadedEvent", e);
        }
    }

    @KafkaListener(topics = {"workflow-task-created", "workflow-task-completed"}, groupId = "audit-service-group")
    @Transactional
    public void handleWorkflowEvents(@Payload BaseEvent event, Acknowledgment acknowledgment) {
        try {
            String action = event instanceof WorkflowTaskCreatedEvent ? "TASK_CREATED" : "TASK_COMPLETED";
            String resourceName = event instanceof WorkflowTaskCreatedEvent 
                    ? "Task: " + ((WorkflowTaskCreatedEvent) event).getTaskType()
                    : "Task: " + ((WorkflowTaskCompletedEvent) event).getTaskStatus();
            Long resourceId = event instanceof WorkflowTaskCreatedEvent 
                    ? ((WorkflowTaskCreatedEvent) event).getTaskId()
                    : ((WorkflowTaskCompletedEvent) event).getTaskId();

            createAuditLog(event.getOrganizationId(), event.getUserId(), action, 
                          "WORKFLOW", resourceId, resourceName, event.getEventId());
            
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing workflow event", e);
        }
    }

    private void createAuditLog(Long organizationId, Long userId, String action, 
                                String resourceType, Long resourceId, String resourceName, String eventId) {
        AuditLog auditLog = new AuditLog();
        auditLog.setOrganizationId(organizationId);
        auditLog.setActorId(userId);
        auditLog.setAction(action);
        auditLog.setTargetType(resourceType);
        auditLog.setTargetId(resourceId);
        auditLog.setDescription(resourceName != null ? resourceName : action + " on " + resourceType);
        auditLog.setIpAddress("KAFKA_EVENT");
        auditLog.setUserAgent("KAFKA_CONSUMER");
        auditLog.setStatus("SUCCESS");
        auditLog.setMetadata("{\"eventId\":\"" + eventId + "\",\"resourceName\":\"" + (resourceName != null ? resourceName : "") + "\"}");
        
        auditLogRepository.save(auditLog);
        logger.info("Created audit log: action={}, resourceType={}, resourceId={}", 
                action, resourceType, resourceId);
    }
}

