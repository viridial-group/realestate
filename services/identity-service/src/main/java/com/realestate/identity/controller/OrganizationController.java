package com.realestate.identity.controller;

import com.realestate.identity.dto.OrganizationDTO;
import com.realestate.identity.entity.Organization;
import com.realestate.identity.mapper.OrganizationMapper;
import com.realestate.identity.service.OrganizationService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/identity/organizations")
@Tag(name = "Organizations", description = "Organization management API")
@SecurityRequirement(name = "bearerAuth")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;

    public OrganizationController(OrganizationService organizationService, OrganizationMapper organizationMapper) {
        this.organizationService = organizationService;
        this.organizationMapper = organizationMapper;
    }

    @PostMapping
    @Operation(summary = "Create organization", description = "Creates a new organization")
    public ResponseEntity<OrganizationDTO> createOrganization(@Valid @RequestBody OrganizationDTO organizationDTO) {
        Organization organization = organizationMapper.toEntity(organizationDTO);
        Organization created = organizationService.createOrganization(organization);
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID", description = "Returns organization information for a specific ID")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        Organization organization = organizationService.getOrganizationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
        return ResponseEntity.ok(organizationMapper.toDTO(organization));
    }

    @GetMapping
    @Operation(summary = "List all organizations", description = "Returns a list of all organizations")
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        List<Organization> organizations = organizationService.getAllOrganizations();
        List<OrganizationDTO> organizationDTOs = organizations.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizationDTOs);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get organizations by user ID", description = "Returns all organizations for a specific user")
    public ResponseEntity<List<OrganizationDTO>> getOrganizationsByUserId(@PathVariable Long userId) {
        List<Organization> organizations = organizationService.getOrganizationsByUserId(userId);
        List<OrganizationDTO> organizationDTOs = organizations.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizationDTOs);
    }

    @GetMapping("/root")
    @Operation(summary = "Get root organizations", description = "Returns all root organizations (without parent)")
    public ResponseEntity<List<OrganizationDTO>> getRootOrganizations() {
        List<Organization> organizations = organizationService.getRootOrganizations();
        List<OrganizationDTO> organizationDTOs = organizations.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizationDTOs);
    }

    @GetMapping("/{parentId}/children")
    @Operation(summary = "Get children organizations", description = "Returns all children organizations for a parent")
    public ResponseEntity<List<OrganizationDTO>> getChildren(@PathVariable Long parentId) {
        List<Organization> children = organizationService.getChildren(parentId);
        List<OrganizationDTO> childrenDTOs = children.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(childrenDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update organization", description = "Updates an existing organization")
    public ResponseEntity<OrganizationDTO> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        Organization organization = organizationMapper.toEntity(organizationDTO);
        Organization updated = organizationService.updateOrganization(id, organization);
        return ResponseEntity.ok(organizationMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete organization", description = "Deletes an organization by ID")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        try {
            organizationService.deleteOrganization(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
