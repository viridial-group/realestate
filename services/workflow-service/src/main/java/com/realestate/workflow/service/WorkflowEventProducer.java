package com.realestate.workflow.service;

import com.realestate.common.event.WorkflowTaskCompletedEvent;
import com.realestate.common.event.WorkflowTaskCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing workflow-related events to Kafka.
 */
@Service
public class WorkflowEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowEventProducer.class);
    
    private static final String WORKFLOW_TASK_CREATED_TOPIC = "workflow-task-created";
    private static final String WORKFLOW_TASK_COMPLETED_TOPIC = "workflow-task-completed";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public WorkflowEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTaskCreated(WorkflowTaskCreatedEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.send(WORKFLOW_TASK_CREATED_TOPIC, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("WorkflowTaskCreatedEvent published: taskId={}", event.getTaskId());
                } else {
                    logger.error("Failed to publish WorkflowTaskCreatedEvent: taskId={}", 
                            event.getTaskId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing WorkflowTaskCreatedEvent: taskId={}", event.getTaskId(), e);
        }
    }

    public void publishTaskCompleted(WorkflowTaskCompletedEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.send(WORKFLOW_TASK_COMPLETED_TOPIC, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("WorkflowTaskCompletedEvent published: taskId={}", event.getTaskId());
                } else {
                    logger.error("Failed to publish WorkflowTaskCompletedEvent: taskId={}", 
                            event.getTaskId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing WorkflowTaskCompletedEvent: taskId={}", event.getTaskId(), e);
        }
    }
}

