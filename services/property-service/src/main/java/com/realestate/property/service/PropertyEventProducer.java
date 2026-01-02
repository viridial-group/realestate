package com.realestate.property.service;

import com.realestate.common.event.PropertyCreatedEvent;
import com.realestate.common.event.PropertyUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing property-related events to Kafka.
 * Conditionnel : ne sera créé que si KafkaTemplate bean existe (Kafka configuré)
 */
@Service
@ConditionalOnBean(KafkaTemplate.class)
public class PropertyEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PropertyEventProducer.class);
    
    private static final String PROPERTY_CREATED_TOPIC = "property-created";
    private static final String PROPERTY_UPDATED_TOPIC = "property-updated";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PropertyEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a PropertyCreatedEvent to Kafka.
     */
    public void publishPropertyCreated(PropertyCreatedEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(PROPERTY_CREATED_TOPIC, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("PropertyCreatedEvent published successfully: propertyId={}, eventId={}", 
                            event.getPropertyId(), event.getEventId());
                } else {
                    logger.error("Failed to publish PropertyCreatedEvent: propertyId={}, eventId={}", 
                            event.getPropertyId(), event.getEventId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing PropertyCreatedEvent: propertyId={}", event.getPropertyId(), e);
        }
    }

    /**
     * Publishes a PropertyUpdatedEvent to Kafka.
     */
    public void publishPropertyUpdated(PropertyUpdatedEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(PROPERTY_UPDATED_TOPIC, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("PropertyUpdatedEvent published successfully: propertyId={}, eventId={}", 
                            event.getPropertyId(), event.getEventId());
                } else {
                    logger.error("Failed to publish PropertyUpdatedEvent: propertyId={}, eventId={}", 
                            event.getPropertyId(), event.getEventId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing PropertyUpdatedEvent: propertyId={}", event.getPropertyId(), e);
        }
    }
}

