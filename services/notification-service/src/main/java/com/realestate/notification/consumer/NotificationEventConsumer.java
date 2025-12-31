package com.realestate.notification.consumer;

import com.realestate.common.event.*;
import com.realestate.notification.entity.Notification;
import com.realestate.notification.repository.NotificationRepository;
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
 * Consumer for all events that trigger notifications.
 */
@Component
public class NotificationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final NotificationRepository notificationRepository;

    public NotificationEventConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "property-created", groupId = "notification-service-group")
    @Transactional
    public void handlePropertyCreated(
            @Payload PropertyCreatedEvent event,
            Acknowledgment acknowledgment) {
        try {
            createNotification(
                    event.getOrganizationId(),
                    event.getUserId(),
                    "PROPERTY_CREATED",
                    "New Property Created",
                    "Property '" + event.getPropertyTitle() + "' has been created",
                    "PROPERTY",
                    event.getPropertyId()
            );
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing PropertyCreatedEvent", e);
        }
    }

    @KafkaListener(topics = "workflow-task-created", groupId = "notification-service-group")
    @Transactional
    public void handleWorkflowTaskCreated(
            @Payload WorkflowTaskCreatedEvent event,
            Acknowledgment acknowledgment) {
        try {
            if (event.getAssignedUserId() != null) {
                createNotification(
                        event.getOrganizationId(),
                        event.getAssignedUserId(),
                        "TASK_ASSIGNED",
                        "New Task Assigned",
                        "You have been assigned a new task: " + event.getTaskType(),
                        event.getTargetType(),
                        event.getTargetId()
                );
            }
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing WorkflowTaskCreatedEvent", e);
        }
    }

    @KafkaListener(topics = "workflow-task-completed", groupId = "notification-service-group")
    @Transactional
    public void handleWorkflowTaskCompleted(
            @Payload WorkflowTaskCompletedEvent event,
            Acknowledgment acknowledgment) {
        try {
            createNotification(
                    event.getOrganizationId(),
                    event.getCompletedByUserId(),
                    "TASK_COMPLETED",
                    "Task Completed",
                    "Task has been " + event.getTaskStatus().toLowerCase(),
                    event.getTargetType(),
                    event.getTargetId()
            );
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing WorkflowTaskCompletedEvent", e);
        }
    }

    @KafkaListener(topics = "document-uploaded", groupId = "notification-service-group")
    @Transactional
    public void handleDocumentUploaded(
            @Payload DocumentUploadedEvent event,
            Acknowledgment acknowledgment) {
        try {
            createNotification(
                    event.getOrganizationId(),
                    event.getUserId(),
                    "DOCUMENT_UPLOADED",
                    "Document Uploaded",
                    "Document '" + event.getDocumentName() + "' has been uploaded",
                    event.getTargetType(),
                    event.getTargetId()
            );
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing DocumentUploadedEvent", e);
        }
    }

    private void createNotification(Long organizationId, Long userId, String type, 
                                   String title, String message, String resourceType, Long resourceId) {
        Notification notification = new Notification();
        notification.setOrganizationId(organizationId);
        notification.setRecipientId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTargetType(resourceType);
        notification.setTargetId(resourceId);
        notification.setStatus("PENDING");
        notification.setChannel("IN_APP");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setActive(true);
        
        notificationRepository.save(notification);
        logger.info("Created notification: type={}, userId={}", type, userId);
    }
}

