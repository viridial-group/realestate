package com.realestate.workflow.consumer;

import com.realestate.common.event.PropertyCreatedEvent;
import com.realestate.workflow.entity.ApprovalWorkflow;
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

/**
 * Consumer for Property-related events.
 * Automatically creates approval workflows when properties are created.
 */
@Component
public class PropertyEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PropertyEventConsumer.class);

    private final ApprovalWorkflowRepository workflowRepository;
    private final TaskRepository taskRepository;

    public PropertyEventConsumer(
            ApprovalWorkflowRepository workflowRepository,
            TaskRepository taskRepository) {
        this.workflowRepository = workflowRepository;
        this.taskRepository = taskRepository;
    }

    @KafkaListener(topics = "property-created", groupId = "workflow-service-group")
    @Transactional
    public void handlePropertyCreated(
            @Payload PropertyCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received PropertyCreatedEvent: propertyId={}, organizationId={}", 
                    event.getPropertyId(), event.getOrganizationId());

            // Create an approval workflow for the new property
            ApprovalWorkflow workflow = new ApprovalWorkflow();
            workflow.setName("Property Approval: " + event.getPropertyReference());
            workflow.setDescription("Automatic approval workflow for property: " + event.getPropertyTitle());
            workflow.setTargetType("PROPERTY");
            workflow.setTargetId(event.getPropertyId());
            workflow.setOrganizationId(event.getOrganizationId());
            workflow.setStatus("PENDING");
            workflow.setCreatedBy(event.getUserId());
            workflow.setCreatedAt(LocalDateTime.now());
            workflow.setActive(true);

            ApprovalWorkflow savedWorkflow = workflowRepository.save(workflow);

            // Create initial task for property review
            Task initialTask = new Task();
            initialTask.setWorkflow(savedWorkflow);
            initialTask.setType("REVIEW");
            initialTask.setTitle("Review Property: " + event.getPropertyTitle());
            initialTask.setDescription("Please review the newly created property");
            initialTask.setStatus("PENDING");
            initialTask.setPriority("MEDIUM");
            initialTask.setCreatedAt(LocalDateTime.now());
            initialTask.setActive(true);

            taskRepository.save(initialTask);

            logger.info("Created approval workflow and initial task for property: propertyId={}, workflowId={}", 
                    event.getPropertyId(), savedWorkflow.getId());

            // Acknowledge the message
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }

        } catch (Exception e) {
            logger.error("Error processing PropertyCreatedEvent: propertyId={}", event.getPropertyId(), e);
            // In production, you might want to send to a dead-letter queue
        }
    }
}

