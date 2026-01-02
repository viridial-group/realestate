package com.realestate.identity.controller;

import com.realestate.identity.dto.OrganizationUserDTO;
import com.realestate.identity.entity.OrganizationUser;
import com.realestate.identity.service.OrganizationUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/identity/organizations/{organizationId}/users")
@Tag(name = "Organization Users", description = "Organization user management API")
@SecurityRequirement(name = "bearerAuth")
public class OrganizationUserController {

    private final OrganizationUserService organizationUserService;

    public OrganizationUserController(OrganizationUserService organizationUserService) {
        this.organizationUserService = organizationUserService;
    }

    @PostMapping("/{userId}")
    @Operation(summary = "Add user to organization", description = "Adds a user to an organization")
    public ResponseEntity<OrganizationUser> addUserToOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "false") Boolean isPrimary) {
        try {
            OrganizationUser organizationUser = organizationUserService.addUserToOrganization(userId, organizationId, isPrimary);
            return ResponseEntity.status(HttpStatus.CREATED).body(organizationUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{userId}/primary")
    @Operation(summary = "Set primary organization", description = "Sets an organization as the primary one for a user")
    public ResponseEntity<OrganizationUser> setPrimaryOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long userId) {
        try {
            OrganizationUser organizationUser = organizationUserService.setPrimaryOrganization(userId, organizationId);
            return ResponseEntity.ok(organizationUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{userId}/teams/{teamId}")
    @Operation(summary = "Add user to team", description = "Adds a user to a team within an organization")
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
    @Operation(summary = "Get users by organization", description = "Returns all users for an organization with user information")
    public ResponseEntity<List<OrganizationUserDTO>> getUsersByOrganization(@PathVariable Long organizationId) {
        List<OrganizationUserDTO> users = organizationUserService.getUsersByOrganizationIdWithUserInfo(organizationId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/teams/{teamId}")
    @Operation(summary = "Get users by team", description = "Returns all users for a team")
    public ResponseEntity<List<OrganizationUser>> getUsersByTeam(@PathVariable Long teamId) {
        List<OrganizationUser> users = organizationUserService.getUsersByTeamId(teamId);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/roles")
    @Operation(summary = "Update user roles", description = "Updates custom roles for a user in an organization")
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
    @Operation(summary = "Remove user from organization", description = "Removes a user from an organization")
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

