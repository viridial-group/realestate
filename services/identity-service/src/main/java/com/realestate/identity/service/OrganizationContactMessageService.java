package com.realestate.identity.service;

import com.realestate.identity.entity.Organization;
import com.realestate.identity.entity.OrganizationContactMessage;
import com.realestate.identity.repository.OrganizationContactMessageRepository;
import com.realestate.identity.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrganizationContactMessageService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationContactMessageService.class);

    private final OrganizationContactMessageRepository contactMessageRepository;
    private final OrganizationRepository organizationRepository;

    public OrganizationContactMessageService(
            OrganizationContactMessageRepository contactMessageRepository,
            OrganizationRepository organizationRepository) {
        this.contactMessageRepository = contactMessageRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public OrganizationContactMessage createContactMessage(OrganizationContactMessage contactMessage) {
        // Charger l'organisation si organizationId est fourni
        if (contactMessage.getOrganizationId() != null) {
            Optional<Organization> organizationOpt = organizationRepository.findById(contactMessage.getOrganizationId());
            if (organizationOpt.isPresent()) {
                Organization organization = organizationOpt.get();
                contactMessage.setOrganization(organization);
            } else {
                logger.warn("Organization not found with ID: {}", contactMessage.getOrganizationId());
            }
        }

        contactMessage.setStatus("NEW");
        contactMessage.setActive(true);

        OrganizationContactMessage saved = contactMessageRepository.save(contactMessage);
        logger.info("Organization contact message created with ID: {}", saved.getId());
        
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<OrganizationContactMessage> getContactMessageById(Long id) {
        return contactMessageRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<OrganizationContactMessage> getAllContactMessages(Pageable pageable) {
        return contactMessageRepository.findAllActive(pageable);
    }

    @Transactional(readOnly = true)
    public Page<OrganizationContactMessage> getContactMessagesByOrganization(Long organizationId, Pageable pageable) {
        return contactMessageRepository.findByOrganizationId(organizationId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OrganizationContactMessage> getContactMessagesByStatus(String status, Pageable pageable) {
        return contactMessageRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<OrganizationContactMessage> getContactMessagesByOrganizationAndStatus(
            Long organizationId, String status, Pageable pageable) {
        return contactMessageRepository.findByOrganizationIdAndStatus(organizationId, status, pageable);
    }

    @Transactional
    public OrganizationContactMessage markAsRead(Long id, Long userId) {
        OrganizationContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));
        
        message.setStatus("READ");
        message.setReadAt(LocalDateTime.now());
        message.setReadBy(userId);
        
        return contactMessageRepository.save(message);
    }

    @Transactional
    public OrganizationContactMessage markAsReplied(Long id, Long userId) {
        OrganizationContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));
        
        message.setStatus("REPLIED");
        message.setRepliedAt(LocalDateTime.now());
        message.setRepliedBy(userId);
        
        return contactMessageRepository.save(message);
    }

    @Transactional
    public OrganizationContactMessage updateNotes(Long id, String notes) {
        OrganizationContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));
        
        message.setNotes(notes);
        
        return contactMessageRepository.save(message);
    }

    @Transactional
    public OrganizationContactMessage archiveMessage(Long id) {
        OrganizationContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));
        
        message.setStatus("ARCHIVED");
        
        return contactMessageRepository.save(message);
    }

    @Transactional
    public void deleteContactMessage(Long id) {
        OrganizationContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));
        
        message.setActive(false);
        contactMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public long countNewMessages() {
        return contactMessageRepository.countNewMessages();
    }

    @Transactional(readOnly = true)
    public long countNewMessagesByOrganization(Long organizationId) {
        return contactMessageRepository.countNewMessagesByOrganizationId(organizationId);
    }
}

