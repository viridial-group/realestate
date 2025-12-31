package com.realestate.document.service;

import com.realestate.common.event.DocumentUploadedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing document-related events to Kafka.
 */
@Service
public class DocumentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(DocumentEventProducer.class);
    
    private static final String DOCUMENT_UPLOADED_TOPIC = "document-uploaded";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DocumentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a DocumentUploadedEvent to Kafka.
     */
    public void publishDocumentUploaded(DocumentUploadedEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(DOCUMENT_UPLOADED_TOPIC, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("DocumentUploadedEvent published successfully: documentId={}, eventId={}", 
                            event.getDocumentId(), event.getEventId());
                } else {
                    logger.error("Failed to publish DocumentUploadedEvent: documentId={}, eventId={}", 
                            event.getDocumentId(), event.getEventId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing DocumentUploadedEvent: documentId={}", event.getDocumentId(), e);
        }
    }
}

