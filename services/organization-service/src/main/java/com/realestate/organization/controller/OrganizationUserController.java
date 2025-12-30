package com.realestate.organization.controller;

import com.realestate.organization.entity.OrganizationUser;
import com.realestate.organization.service.OrganizationUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizations/{organizationId}/users")
public class OrganizationUserController {

    private final OrganizationUserService organizationUserService;

    public OrganizationUserController(OrganizationUserService organizationUserService) {
        this.organizationUserService = organizationUserService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<OrganizationUser> addUserToOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long userId) {
        try {
            OrganizationUser organizationUser = organizationUserService.addUserToOrganization(userId, organizationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(organizationUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{userId}/teams/{teamId}")
    public ResponseEntity<OrganizationUser> addUserToTeam(
            @PathVariable Long organizationId,
            @PathVariable Long userId,
            @PathVariable Long teamId) {
        try {
            OrganizationUser organizationUser = organizationUserService.addUserToTeam(userId, organizationId, teamId);
            return ResponseEntity.ok(organizationUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrganizationUser>> getUsersByOrganization(@PathVariable Long organizationId) {
        List<OrganizationUser> users = organizationUserService.getUsersByOrganizationId(organizationId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<List<OrganizationUser>> getUsersByTeam(@PathVariable Long teamId) {
        List<OrganizationUser> users = organizationUserService.getUsersByTeamId(teamId);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<OrganizationUser> updateUserRoles(
            @PathVariable Long organizationId,
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        try {
            String customRoles = request.get("customRoles");
            OrganizationUser updated = organizationUserService.updateUserRoles(userId, organizationId, customRoles);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUserFromOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long userId) {
        try {
            organizationUserService.removeUserFromOrganization(userId, organizationId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

