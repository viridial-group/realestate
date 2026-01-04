package com.realestate.property.service;

import com.realestate.property.entity.ContactMessage;
import com.realestate.property.entity.Property;
import com.realestate.property.repository.ContactMessageRepository;
import com.realestate.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContactMessageService {

    private static final Logger logger = LoggerFactory.getLogger(ContactMessageService.class);

    private final ContactMessageRepository contactMessageRepository;
    private final PropertyRepository propertyRepository;
    private final RestTemplate restTemplate;
    
    @Value("${services.notification.url:http://localhost:8085}")
    private String notificationServiceUrl;

    public ContactMessageService(
            ContactMessageRepository contactMessageRepository,
            PropertyRepository propertyRepository,
            @Autowired(required = false) RestTemplate restTemplate) {
        this.contactMessageRepository = contactMessageRepository;
        this.propertyRepository = propertyRepository;
        this.restTemplate = restTemplate != null ? restTemplate : new RestTemplate();
    }

    @Transactional
    public ContactMessage createContactMessage(ContactMessage contactMessage) {
        Property property = null;
        Long assignedUserId = null;
        
        // Si une propriété est associée, la charger
        if (contactMessage.getPropertyId() != null) {
            Optional<Property> propertyOpt = propertyRepository.findById(contactMessage.getPropertyId());
            if (propertyOpt.isPresent()) {
                property = propertyOpt.get();
                contactMessage.setProperty(property);
                // Récupérer l'organizationId de la propriété si non défini
                if (contactMessage.getOrganizationId() == null && property.getOrganizationId() != null) {
                    contactMessage.setOrganizationId(property.getOrganizationId());
                }
                // Récupérer l'utilisateur assigné à la propriété
                assignedUserId = property.getAssignedUserId();
            }
        }

        contactMessage.setStatus("NEW");
        contactMessage.setActive(true);

        ContactMessage saved = contactMessageRepository.save(contactMessage);
        logger.info("Contact message created with ID: {}", saved.getId());
        
        // Envoyer une notification à l'utilisateur assigné à la propriété
        if (assignedUserId != null && property != null) {
            sendNotificationToAssignedUser(saved, property, assignedUserId);
        }
        
        return saved;
    }
    
    /**
     * Envoie une notification à l'utilisateur assigné à la propriété
     */
    private void sendNotificationToAssignedUser(ContactMessage message, Property property, Long assignedUserId) {
        try {
            String url = notificationServiceUrl + "/api/notifications/send";
            
            Map<String, Object> notificationRequest = new HashMap<>();
            notificationRequest.put("type", "CONTACT_MESSAGE");
            notificationRequest.put("title", "Nouveau message de contact");
            notificationRequest.put("message", 
                String.format("Nouveau message de %s concernant la propriété \"%s\"", 
                    message.getName(), property.getTitle()));
            notificationRequest.put("recipientId", assignedUserId);
            notificationRequest.put("organizationId", property.getOrganizationId());
            notificationRequest.put("channel", "IN_APP");
            notificationRequest.put("targetType", "CONTACT_MESSAGE");
            notificationRequest.put("targetId", message.getId());
            notificationRequest.put("actionUrl", "/contacts?messageId=" + message.getId());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(notificationRequest, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Notification sent successfully to user {} for contact message {}", 
                    assignedUserId, message.getId());
            } else {
                logger.warn("Failed to send notification to user {}: {}", assignedUserId, response.getStatusCode());
            }
        } catch (Exception e) {
            // Ne pas faire échouer la création du message si la notification échoue
            logger.error("Error sending notification to user {} for contact message {}: {}", 
                assignedUserId, message.getId(), e.getMessage());
        }
    }

    public Page<ContactMessage> getAllContactMessages(Pageable pageable) {
        return contactMessageRepository.findAllActive(pageable);
    }

    public Page<ContactMessage> getContactMessagesByOrganization(Long organizationId, Pageable pageable) {
        return contactMessageRepository.findByOrganizationId(organizationId, pageable);
    }

    public Page<ContactMessage> getContactMessagesByStatus(String status, Pageable pageable) {
        return contactMessageRepository.findByStatus(status, pageable);
    }

    public Page<ContactMessage> getContactMessagesByOrganizationAndStatus(
            Long organizationId, String status, Pageable pageable) {
        return contactMessageRepository.findByOrganizationIdAndStatus(organizationId, status, pageable);
    }

    public List<ContactMessage> getContactMessagesByProperty(Long propertyId) {
        return contactMessageRepository.findByPropertyId(propertyId);
    }

    public Optional<ContactMessage> getContactMessageById(Long id) {
        return contactMessageRepository.findById(id);
    }

    @Transactional
    public ContactMessage markAsRead(Long id, Long userId) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));

        if (message.getStatus().equals("NEW")) {
            message.setStatus("READ");
            message.setReadAt(LocalDateTime.now());
            message.setReadBy(userId);
        }

        return contactMessageRepository.save(message);
    }

    @Transactional
    public ContactMessage markAsReplied(Long id, Long userId) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));

        message.setStatus("REPLIED");
        message.setRepliedAt(LocalDateTime.now());
        message.setRepliedBy(userId);

        return contactMessageRepository.save(message);
    }

    @Transactional
    public ContactMessage updateNotes(Long id, String notes) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));

        message.setNotes(notes);
        return contactMessageRepository.save(message);
    }

    @Transactional
    public ContactMessage archiveMessage(Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));

        message.setStatus("ARCHIVED");
        return contactMessageRepository.save(message);
    }

    @Transactional
    public void deleteContactMessage(Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));

        message.setActive(false);
        contactMessageRepository.save(message);
    }

    public long countNewMessages() {
        return contactMessageRepository.countNewMessages();
    }

    public long countNewMessagesByOrganization(Long organizationId) {
        return contactMessageRepository.countNewMessagesByOrganizationId(organizationId);
    }

    public Page<ContactMessage> getContactMessagesByAssignedUser(Long assignedUserId, Pageable pageable) {
        return contactMessageRepository.findByAssignedUserId(assignedUserId, pageable);
    }

    public Page<ContactMessage> getContactMessagesByAssignedUserAndStatus(
            Long assignedUserId, String status, Pageable pageable) {
        return contactMessageRepository.findByAssignedUserIdAndStatus(assignedUserId, status, pageable);
    }

    public long countNewMessagesByAssignedUser(Long assignedUserId) {
        return contactMessageRepository.countNewMessagesByAssignedUserId(assignedUserId);
    }

    /**
     * Count unread messages for a single property
     */
    public long countUnreadMessagesByPropertyId(Long propertyId) {
        return contactMessageRepository.countUnreadMessagesByPropertyId(propertyId);
    }

    /**
     * Count unread messages for multiple properties (batch operation)
     * Returns a map with propertyId as key and count as value
     */
    public Map<Long, Long> countUnreadMessagesByPropertyIds(List<Long> propertyIds) {
        if (propertyIds == null || propertyIds.isEmpty()) {
            return new HashMap<>();
        }
        
        List<Object[]> results = contactMessageRepository.countUnreadMessagesByPropertyIds(propertyIds);
        Map<Long, Long> counts = new HashMap<>();
        
        // Initialize all property IDs with 0
        for (Long propertyId : propertyIds) {
            counts.put(propertyId, 0L);
        }
        
        // Fill in actual counts
        for (Object[] result : results) {
            Long propertyId = ((Number) result[0]).longValue();
            Long count = ((Number) result[1]).longValue();
            counts.put(propertyId, count);
        }
        
        return counts;
    }
}

