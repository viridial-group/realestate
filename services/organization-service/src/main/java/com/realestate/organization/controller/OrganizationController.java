package com.realestate.organization.controller;

import com.realestate.organization.entity.Organization;
import com.realestate.organization.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<Organization> createOrganization(@Valid @RequestBody Organization organization) {
        Organization created = organizationService.createOrganization(organization);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Organization>> getAllOrganizations() {
        List<Organization> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Organization>> getOrganizationsByUserId(@PathVariable Long userId) {
        List<Organization> organizations = organizationService.getOrganizationsByUserId(userId);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/root")
    public ResponseEntity<List<Organization>> getRootOrganizations() {
        List<Organization> organizations = organizationService.getRootOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<Organization>> getChildren(@PathVariable Long parentId) {
        List<Organization> children = organizationService.getChildren(parentId);
        return ResponseEntity.ok(children);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Organization> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody Organization organizationDetails) {
        try {
            Organization updated = organizationService.updateOrganization(id, organizationDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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

