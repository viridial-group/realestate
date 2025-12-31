package com.realestate.emailing.consumer;

import com.realestate.common.client.IdentityServiceClient;
import com.realestate.common.client.dto.UserInfoDTO;
import com.realestate.common.event.WorkflowTaskCreatedEvent;
import com.realestate.common.event.WorkflowTaskCompletedEvent;
import com.realestate.emailing.entity.Email;
import com.realestate.emailing.entity.EmailTemplate;
import com.realestate.emailing.repository.EmailRepository;
import com.realestate.emailing.repository.EmailTemplateRepository;
import com.realestate.emailing.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Consumer for events that trigger emails.
 */
@Component
public class EmailEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EmailEventConsumer.class);

    private final EmailService emailService;
    private final EmailTemplateRepository templateRepository;
    private final EmailRepository emailRepository;
    private final IdentityServiceClient identityServiceClient;
    private final String serviceAuthToken; // Internal service token for inter-service calls

    public EmailEventConsumer(
            EmailService emailService,
            EmailTemplateRepository templateRepository,
            EmailRepository emailRepository,
            IdentityServiceClient identityServiceClient,
            @Value("${services.internal.auth-token:}") String serviceAuthToken) {
        this.emailService = emailService;
        this.templateRepository = templateRepository;
        this.emailRepository = emailRepository;
        this.identityServiceClient = identityServiceClient;
        this.serviceAuthToken = serviceAuthToken;
    }

    @KafkaListener(topics = "workflow-task-created", groupId = "emailing-service-group")
    @Transactional
    public void handleWorkflowTaskCreated(@Payload WorkflowTaskCreatedEvent event, Acknowledgment acknowledgment) {
        try {
            if (event.getAssignedUserId() != null) {
                // Get user email from Identity Service
                String userEmail = getUserEmail(event.getAssignedUserId());
                
                if (userEmail == null || userEmail.isEmpty()) {
                    logger.warn("Could not retrieve email for user {}", event.getAssignedUserId());
                    if (acknowledgment != null) acknowledgment.acknowledge();
                    return;
                }

                // Find email template for task assignment
                Optional<EmailTemplate> templateOpt = templateRepository
                        .findActiveByNameAndOrganization("TASK_ASSIGNED", event.getOrganizationId());
                
                if (templateOpt.isPresent()) {
                    EmailTemplate template = templateOpt.get();
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("taskTitle", event.getTaskType());
                    variables.put("taskType", event.getTaskType());
                    variables.put("targetType", event.getTargetType());
                    
                    // Send email using template
                    emailService.sendEmailFromTemplate(
                            "TASK_ASSIGNED",
                            userEmail,
                            event.getAssignedUserId(),
                            event.getOrganizationId(),
                            variables
                    );
                } else {
                    // Fallback: send simple email
                    Email email = new Email();
                    email.setOrganizationId(event.getOrganizationId());
                    email.setRecipientEmail(userEmail);
                    email.setRecipientId(event.getAssignedUserId());
                    email.setSubject("New Task Assigned");
                    email.setBody("You have been assigned a new task: " + event.getTaskType());
                    email.setStatus("PENDING");
                    email.setCreatedAt(LocalDateTime.now());
                    
                    emailService.sendEmail(email);
                }
            }
            
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing WorkflowTaskCreatedEvent", e);
        }
    }

    @KafkaListener(topics = "workflow-task-completed", groupId = "emailing-service-group")
    @Transactional
    public void handleWorkflowTaskCompleted(@Payload WorkflowTaskCompletedEvent event, Acknowledgment acknowledgment) {
        try {
            if (event.getCompletedByUserId() == null) {
                logger.warn("WorkflowTaskCompletedEvent has no completedByUserId");
                if (acknowledgment != null) acknowledgment.acknowledge();
                return;
            }

            // Get user email from Identity Service
            String userEmail = getUserEmail(event.getCompletedByUserId());
            
            if (userEmail == null || userEmail.isEmpty()) {
                logger.warn("Could not retrieve email for user {}", event.getCompletedByUserId());
                if (acknowledgment != null) acknowledgment.acknowledge();
                return;
            }

            // Find email template for task completion
            Optional<EmailTemplate> templateOpt = templateRepository
                    .findActiveByNameAndOrganization("TASK_COMPLETED", event.getOrganizationId());
            
            if (templateOpt.isPresent()) {
                EmailTemplate template = templateOpt.get();
                Map<String, Object> variables = new HashMap<>();
                variables.put("taskStatus", event.getTaskStatus());
                variables.put("comments", event.getComments() != null ? event.getComments() : "");
                
                // Send email using template
                emailService.sendEmailFromTemplate(
                        "TASK_COMPLETED",
                        userEmail,
                        event.getCompletedByUserId(),
                        event.getOrganizationId(),
                        variables
                );
            }
            
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing WorkflowTaskCompletedEvent", e);
        }
    }

    /**
     * Get user email from Identity Service
     */
    private String getUserEmail(Long userId) {
        try {
            Optional<UserInfoDTO> userOpt = identityServiceClient.getUserById(userId, serviceAuthToken)
                    .block(); // Blocking call - acceptable in Kafka consumer context
            
            if (userOpt.isPresent() && userOpt.get().getEmail() != null) {
                return userOpt.get().getEmail();
            }
        } catch (Exception e) {
            logger.error("Error fetching user email for user {}: {}", userId, e.getMessage());
        }
        return null;
    }
}

