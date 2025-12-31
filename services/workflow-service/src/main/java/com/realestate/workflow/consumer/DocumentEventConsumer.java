package com.realestate.workflow.consumer;

import com.realestate.common.event.DocumentUploadedEvent;
import com.realestate.workflow.entity.Task;
import com.realestate.workflow.repository.ApprovalWorkflowRepository;
import com.realestate.workflow.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Consumer for Document-related events.
 * Creates tasks in existing workflows when documents are uploaded.
 */
@Component
public class DocumentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DocumentEventConsumer.class);

    private final ApprovalWorkflowRepository workflowRepository;
    private final TaskRepository taskRepository;

    public DocumentEventConsumer(
            ApprovalWorkflowRepository workflowRepository,
            TaskRepository taskRepository) {
        this.workflowRepository = workflowRepository;
        this.taskRepository = taskRepository;
    }

    @KafkaListener(topics = "document-uploaded", groupId = "workflow-service-group")
    @Transactional
    public void handleDocumentUploaded(
            @Payload DocumentUploadedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received DocumentUploadedEvent: documentId={}, targetType={}, targetId={}", 
                    event.getDocumentId(), event.getTargetType(), event.getTargetId());

            // Find workflows related to this document's target
            List<com.realestate.workflow.entity.ApprovalWorkflow> workflows = 
                    workflowRepository.findActiveByTarget(
                            event.getTargetType(), event.getTargetId());

            for (com.realestate.workflow.entity.ApprovalWorkflow workflow : workflows) {
                // Create a task for document review
                Task documentTask = new Task();
                documentTask.setWorkflow(workflow);
                documentTask.setType("DOCUMENT_REVIEW");
                documentTask.setTitle("Review Document: " + event.getDocumentName());
                documentTask.setDescription("A new document has been uploaded: " + event.getDocumentName());
                documentTask.setStatus("PENDING");
                documentTask.setPriority("MEDIUM");
                documentTask.setCreatedAt(LocalDateTime.now());
                documentTask.setActive(true);

                taskRepository.save(documentTask);

                logger.info("Created document review task: taskId={}, workflowId={}, documentId={}", 
                        documentTask.getId(), workflow.getId(), event.getDocumentId());
            }

            // Acknowledge the message
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }

        } catch (Exception e) {
            logger.error("Error processing DocumentUploadedEvent: documentId={}", event.getDocumentId(), e);
        }
    }
}

