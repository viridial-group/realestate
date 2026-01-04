package com.realestate.identity.controller;

import com.realestate.identity.dto.OrganizationContactMessageDTO;
import com.realestate.identity.entity.OrganizationContactMessage;
import com.realestate.identity.mapper.OrganizationContactMessageMapper;
import com.realestate.identity.service.OrganizationContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identity/organization-contact-messages")
@Tag(name = "Organization Contact Messages", description = "Organization contact message management API")
@SecurityRequirement(name = "bearerAuth")
public class OrganizationContactMessageController {

    private final OrganizationContactMessageService contactMessageService;
    private final OrganizationContactMessageMapper contactMessageMapper;

    public OrganizationContactMessageController(
            OrganizationContactMessageService contactMessageService,
            OrganizationContactMessageMapper contactMessageMapper) {
        this.contactMessageService = contactMessageService;
        this.contactMessageMapper = contactMessageMapper;
    }

    @GetMapping
    @Operation(summary = "List contact messages", description = "Returns a paginated list of organization contact messages")
    public ResponseEntity<Page<OrganizationContactMessageDTO>> getContactMessages(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OrganizationContactMessage> messages;

        if (organizationId != null && status != null) {
            messages = contactMessageService.getContactMessagesByOrganizationAndStatus(
                    organizationId, status, pageable);
        } else if (organizationId != null) {
            messages = contactMessageService.getContactMessagesByOrganization(organizationId, pageable);
        } else if (status != null) {
            messages = contactMessageService.getContactMessagesByStatus(status, pageable);
        } else {
            messages = contactMessageService.getAllContactMessages(pageable);
        }

        Page<OrganizationContactMessageDTO> dtoPage = messages.map(contactMessageMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contact message by ID", description = "Returns contact message information")
    public ResponseEntity<OrganizationContactMessageDTO> getContactMessageById(@PathVariable Long id) {
        OrganizationContactMessage message = contactMessageService.getContactMessageById(id)
                .orElseThrow(() -> new RuntimeException("Contact message not found: " + id));
        return ResponseEntity.ok(contactMessageMapper.toDTO(message));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark message as read", description = "Marks a contact message as read")
    public ResponseEntity<OrganizationContactMessageDTO> markAsRead(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        OrganizationContactMessage message = contactMessageService.markAsRead(id, userId);
        return ResponseEntity.ok(contactMessageMapper.toDTO(message));
    }

    @PutMapping("/{id}/replied")
    @Operation(summary = "Mark message as replied", description = "Marks a contact message as replied")
    public ResponseEntity<OrganizationContactMessageDTO> markAsReplied(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        OrganizationContactMessage message = contactMessageService.markAsReplied(id, userId);
        return ResponseEntity.ok(contactMessageMapper.toDTO(message));
    }

    @PutMapping("/{id}/notes")
    @Operation(summary = "Update message notes", description = "Updates internal notes for a contact message")
    public ResponseEntity<OrganizationContactMessageDTO> updateNotes(
            @PathVariable Long id,
            @RequestBody String notes) {
        OrganizationContactMessage message = contactMessageService.updateNotes(id, notes);
        return ResponseEntity.ok(contactMessageMapper.toDTO(message));
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "Archive message", description = "Archives a contact message")
    public ResponseEntity<OrganizationContactMessageDTO> archiveMessage(@PathVariable Long id) {
        OrganizationContactMessage message = contactMessageService.archiveMessage(id);
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
            @RequestParam(required = false) Long organizationId) {
        long count;
        if (organizationId != null) {
            count = contactMessageService.countNewMessagesByOrganization(organizationId);
        } else {
            count = contactMessageService.countNewMessages();
        }
        return ResponseEntity.ok(count);
    }
}

