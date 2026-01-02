package com.realestate.property.consumer;

import com.realestate.common.document.PropertyDocument;
import com.realestate.common.event.PropertyCreatedEvent;
import com.realestate.common.event.PropertyUpdatedEvent;
import com.realestate.common.repository.elasticsearch.PropertyDocumentRepository;
import com.realestate.property.entity.Property;
import com.realestate.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Consumer Kafka pour indexer les Properties dans Elasticsearch
 * 
 * Ce consumer n'est créé que si:
 * 1. PropertyDocumentRepository (Elasticsearch) bean existe
 * 2. Les classes d'événements Kafka sont dans le classpath
 * 3. Kafka est configuré (vérifié via @ConditionalOnBean sur PropertyDocumentRepository)
 * 
 * Utilise @ConditionalOnBean pour vérifier l'existence du repository Elasticsearch.
 * Les dépendances sont optionnelles pour permettre le démarrage sans Elasticsearch.
 */
@Component
@ConditionalOnBean(PropertyDocumentRepository.class)
@ConditionalOnClass(name = {
    "com.realestate.common.event.PropertyCreatedEvent",
    "com.realestate.common.event.PropertyUpdatedEvent"
})
public class PropertyIndexingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PropertyIndexingConsumer.class);

    private final PropertyDocumentRepository propertyDocumentRepository;
    private final PropertyRepository propertyRepository;

    public PropertyIndexingConsumer(
            @Autowired(required = false) PropertyDocumentRepository propertyDocumentRepository,
            PropertyRepository propertyRepository) {
        this.propertyDocumentRepository = propertyDocumentRepository;
        this.propertyRepository = propertyRepository;
    }

    @KafkaListener(topics = "property-created", groupId = "property-indexing-group")
    @Transactional
    public void handlePropertyCreated(@Payload PropertyCreatedEvent event, Acknowledgment acknowledgment) {
        if (propertyDocumentRepository == null) {
            logger.debug("PropertyDocumentRepository not available, skipping indexing");
            if (acknowledgment != null) acknowledgment.acknowledge();
            return;
        }
        
        try {
            Optional<Property> propertyOpt = propertyRepository.findById(event.getPropertyId());
            
            if (propertyOpt.isPresent()) {
                Property property = propertyOpt.get();
                PropertyDocument document = convertToDocument(property);
                propertyDocumentRepository.save(document);
                logger.info("Indexed property {} in Elasticsearch", event.getPropertyId());
            } else {
                logger.warn("Property {} not found for indexing", event.getPropertyId());
            }
            
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error indexing property {}: {}", event.getPropertyId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "property-updated", groupId = "property-indexing-group")
    @Transactional
    public void handlePropertyUpdated(@Payload PropertyUpdatedEvent event, Acknowledgment acknowledgment) {
        if (propertyDocumentRepository == null) {
            logger.debug("PropertyDocumentRepository not available, skipping indexing");
            if (acknowledgment != null) acknowledgment.acknowledge();
            return;
        }
        
        try {
            Optional<Property> propertyOpt = propertyRepository.findById(event.getPropertyId());
            
            if (propertyOpt.isPresent()) {
                Property property = propertyOpt.get();
                PropertyDocument document = convertToDocument(property);
                propertyDocumentRepository.save(document);
                logger.info("Updated property {} in Elasticsearch", event.getPropertyId());
            } else {
                logger.warn("Property {} not found for indexing", event.getPropertyId());
            }
            
            if (acknowledgment != null) acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error updating property {} in Elasticsearch: {}", event.getPropertyId(), e.getMessage(), e);
        }
    }

    /**
     * Convert Property entity to PropertyDocument
     */
    private PropertyDocument convertToDocument(Property property) {
        PropertyDocument document = new PropertyDocument();
        document.setId(property.getId());
        document.setReference(property.getReference());
        document.setTitle(property.getTitle());
        document.setDescription(property.getDescription());
        document.setType(property.getType());
        document.setStatus(property.getStatus());
        document.setPrice(property.getPrice());
        document.setCurrency(property.getCurrency());
        document.setSurface(property.getSurface());
        document.setRooms(property.getRooms());
        document.setBedrooms(property.getBedrooms());
        document.setBathrooms(property.getBathrooms());
        document.setAddress(property.getAddress());
        document.setCity(property.getCity());
        document.setPostalCode(property.getPostalCode());
        document.setCountry(property.getCountry());
        document.setLatitude(property.getLatitude());
        document.setLongitude(property.getLongitude());
        document.setOrganizationId(property.getOrganizationId());
        document.setTeamId(property.getTeamId());
        document.setAssignedUserId(property.getAssignedUserId());
        document.setCreatedBy(property.getCreatedBy());
        document.setCreatedAt(property.getCreatedAt());
        document.setUpdatedAt(property.getUpdatedAt());
        document.setActive(property.getActive());
        return document;
    }
}

