package com.realestate.user.organization.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.user.organization.entity.Organization;
import com.realestate.user.organization.service.OrganizationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService service;

    @GetMapping("/{id}/sub-tenants")
    public List<Organization> getSubTenants(@PathVariable UUID id) {
        return service.getSubTree(id);
    }

    @GetMapping("/{id}/subtree-ids")
    public List<UUID> getSubTreeIds(@PathVariable UUID id) {
        return service.getSubTreeIds(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable UUID id) {
        Organization org = service.getById(id);
        return ResponseEntity.ok(org);
    }

    @GetMapping("/{organizationId}/users")
    public ResponseEntity<List<com.realestate.user.entity.User>> getUsersByOrganizationId(@PathVariable UUID organizationId) {
        List<com.realestate.user.entity.User> users = service.getUsersByOrganizationId(organizationId);
        return ResponseEntity.ok(users);
    }
}

