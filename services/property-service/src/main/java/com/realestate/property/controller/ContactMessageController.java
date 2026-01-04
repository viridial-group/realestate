package com.realestate.property.controller;

import com.realestate.property.dto.ContactMessageDTO;
import com.realestate.property.entity.ContactMessage;
import com.realestate.property.mapper.ContactMessageMapper;
import com.realestate.property.service.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contact-messages")
@Tag(name = "Contact Messages", description = "Contact message management API")
public class ContactMessageController {

    private final ContactMessageService contactMessageService;
    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageController(
            ContactMessageService contactMessageService,
            ContactMessageMapper contactMessageMapper) {
        this.contactMessageService = contactMessageService;
        this.contactMessageMapper = contactMessageMapper;
    }

    @PostMapping
    @Operation(summary = "Create contact message", description = "Creates a new contact message from public form")
    public ResponseEntity<ContactMessageDTO> createContactMessage(
            @Valid @RequestBody ContactMessageDTO contactMessageDTO) {
        ContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDTO);
        ContactMessage created = contactMessageService.createContactMessage(contactMessage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactMessageMapper.toDTO(created));
    }

    @GetMapping
    @Operation(summary = "List contact messages", description = "Returns a paginated list of contact messages")
    public ResponseEntity<Page<ContactMessageDTO>> getContactMessages(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactMessage> messages;

        // Priorité au filtre par utilisateur assigné
        if (assignedUserId != null) {
            if (status != null) {
                messages = contactMessageService.getContactMessagesByAssignedUserAndStatus(
                        assignedUserId, status, pageable);
            } else {
                messages = contactMessageService.getContactMessagesByAssignedUser(assignedUserId, pageable);
            }
        } else if (organizationId != null && status != null) {
            messages = contactMessageService.getContactMessagesByOrganizationAndStatus(
                    organizationId, status, pageable);
        } else if (organizationId != null) {
            messages = contactMessageService.getContactMessagesByOrganization(organizationId, pageable);
        } else if (status != null) {
            messages = contactMessageService.getContactMessagesByStatus(status, pageable);
        } else {
            messages = contactMessageService.getAllContactMessages(pageable);
        }

        Page<ContactMessageDTO> dtoPage = messages.map(contactMessageMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contact message by ID", description = "Returns contact message information")
    public ResponseEntity<ContactMessageDTO> getContactMessageById(@PathVariable Long id) {
        ContactMessage message = contactMessageService.getContactMessageById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));
        return ResponseEntity.ok(contactMessageMapper.toDTO(message));
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get contact messages by property", description = "Returns all contact messages for a specific property")
    public ResponseEntity<List<ContactMessageDTO>> getContactMessagesByProperty(@PathVariable Long propertyId) {
        List<ContactMessage> messages = contactMessageService.getContactMessagesByProperty(propertyId);
        List<ContactMessageDTO> dtos = messages.stream()
                .map(contactMessageMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark message as read", description = "Marks a contact message as read")
    public ResponseEntity<ContactMessageDTO> markAsRead(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        ContactMessage message = contactMessageService.markAsRead(id, userId);
        return ResponseEntity.ok(contactMessageMapper.toDTO(message));
    }

    @PutMapping("/{id}/replied")
    @Operation(summary = "Mark message as replied", description = "Marks a contact message as replied")
    public ResponseEntity<ContactMessageDTO> markAsReplied(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        ContactMessage message = contactMessageService.markAsReplied(id, userId);
        return ResponseEntity.ok(contactMessageMapper.toDTO(message));
    }

    @PutMapping("/{id}/notes")
    @Operation(summary = "Update message notes", description = "Updates internal notes for a contact message")
    public ResponseEntity<ContactMessageDTO> updateNotes(
            @PathVariable Long id,
            @RequestBody String notes) {
        ContactMessage message = contactMessageService.updateNotes(id, notes);
        return ResponseEntity.ok(contactMessageMapper.toDTO(message));
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "Archive message", description = "Archives a contact message")
    public ResponseEntity<ContactMessageDTO> archiveMessage(@PathVariable Long id) {
        ContactMessage message = contactMessageService.archiveMessage(id);
        return ResponseEntity.ok(contactMessageMapper.toDTO(message));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete contact message", description = "Soft deletes a contact message")
    public ResponseEntity<Void> deleteContactMessage(@PathVariable Long id) {
        contactMessageService.deleteContactMessage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/new-count")
    @Operation(summary = "Get count of new messages", description = "Returns the count of unread contact messages")
    public ResponseEntity<Long> getNewMessagesCount(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Long assignedUserId) {
        long count;
        if (assignedUserId != null) {
            count = contactMessageService.countNewMessagesByAssignedUser(assignedUserId);
        } else if (organizationId != null) {
            count = contactMessageService.countNewMessagesByOrganization(organizationId);
        } else {
            count = contactMessageService.countNewMessages();
        }
        return ResponseEntity.ok(count);
    }
}

