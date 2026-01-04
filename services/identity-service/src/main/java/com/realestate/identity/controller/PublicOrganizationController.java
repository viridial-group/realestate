package com.realestate.identity.controller;

import com.realestate.identity.dto.OrganizationContactMessageDTO;
import com.realestate.identity.dto.OrganizationDTO;
import com.realestate.identity.entity.Organization;
import com.realestate.identity.entity.OrganizationContactMessage;
import com.realestate.identity.mapper.OrganizationContactMessageMapper;
import com.realestate.identity.mapper.OrganizationMapper;
import com.realestate.identity.service.OrganizationContactMessageService;
import com.realestate.identity.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/organizations")
@Tag(name = "Public Organizations", description = "Public API for viewing organizations (agencies)")
public class PublicOrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;
    private final OrganizationContactMessageService contactMessageService;
    private final OrganizationContactMessageMapper contactMessageMapper;

    public PublicOrganizationController(
            OrganizationService organizationService,
            OrganizationMapper organizationMapper,
            OrganizationContactMessageService contactMessageService,
            OrganizationContactMessageMapper contactMessageMapper) {
        this.organizationService = organizationService;
        this.organizationMapper = organizationMapper;
        this.contactMessageService = contactMessageService;
        this.contactMessageMapper = contactMessageMapper;
    }

    @GetMapping
    @Operation(summary = "Get all active organizations", description = "Returns a list of all active organizations (agencies)")
    public ResponseEntity<List<OrganizationDTO>> getAllActiveOrganizations() {
        List<Organization> organizations = organizationService.getAllOrganizations()
                .stream()
                .filter(org -> org.getActive() != null && org.getActive())
                .collect(Collectors.toList());
        
        List<OrganizationDTO> organizationDTOs = organizations.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(organizationDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID", description = "Returns organization information for a specific ID")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id)
                .map(organizationMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{organizationId}/contact")
    @Operation(summary = "Create contact message for organization", description = "Creates a new contact message for a real estate agency")
    public ResponseEntity<OrganizationContactMessageDTO> createContactMessage(
            @PathVariable Long organizationId,
            @Valid @RequestBody OrganizationContactMessageDTO contactMessageDTO) {
        
        contactMessageDTO.setOrganizationId(organizationId);
        OrganizationContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDTO);
        OrganizationContactMessage created = contactMessageService.createContactMessage(contactMessage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactMessageMapper.toDTO(created));
    }
}

