package com.realestate.organization.controller;

import com.realestate.organization.dto.OrganizationDTO;
import com.realestate.organization.entity.Organization;
import com.realestate.organization.mapper.OrganizationMapper;
import com.realestate.organization.service.OrganizationService;
import com.realestate.common.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;

    public OrganizationController(OrganizationService organizationService, OrganizationMapper organizationMapper) {
        this.organizationService = organizationService;
        this.organizationMapper = organizationMapper;
    }

    @PostMapping
    public ResponseEntity<OrganizationDTO> createOrganization(@Valid @RequestBody OrganizationDTO organizationDTO) {
        Organization organization = organizationMapper.toEntity(organizationDTO);
        Organization created = organizationService.createOrganization(organization);
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        Organization organization = organizationService.getOrganizationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
        return ResponseEntity.ok(organizationMapper.toDTO(organization));
    }

    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        List<Organization> organizations = organizationService.getAllOrganizations();
        List<OrganizationDTO> organizationDTOs = organizations.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizationDTOs);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrganizationDTO>> getOrganizationsByUserId(@PathVariable Long userId) {
        List<Organization> organizations = organizationService.getOrganizationsByUserId(userId);
        List<OrganizationDTO> organizationDTOs = organizations.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizationDTOs);
    }

    @GetMapping("/root")
    public ResponseEntity<List<OrganizationDTO>> getRootOrganizations() {
        List<Organization> organizations = organizationService.getRootOrganizations();
        List<OrganizationDTO> organizationDTOs = organizations.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizationDTOs);
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<OrganizationDTO>> getChildren(@PathVariable Long parentId) {
        List<Organization> children = organizationService.getChildren(parentId);
        List<OrganizationDTO> childrenDTOs = children.stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(childrenDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDTO> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationDTO organizationDTO) {
        Organization organization = organizationMapper.toEntity(organizationDTO);
        Organization updated = organizationService.updateOrganization(id, organization);
        return ResponseEntity.ok(organizationMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        try {
            organizationService.deleteOrganization(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

