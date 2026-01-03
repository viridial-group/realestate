package com.realestate.identity.controller;

import com.realestate.identity.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/identity/users")
@Tag(name = "User Roles", description = "User role assignment API")
@SecurityRequirement(name = "bearerAuth")
public class UserRoleController {

    private final RoleService roleService;

    public UserRoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/assign-roles")
    @Operation(summary = "Assign roles to user", description = "Assigns one or more roles to a user")
    public ResponseEntity<Void> assignRolesToUser(@RequestBody UserRoleAssignmentRequest request) {
        roleService.assignRolesToUser(request.getUserId(), request.getRoleIds());
        return ResponseEntity.ok().build();
    }

    // DTO pour la requête
    public static class UserRoleAssignmentRequest {
        private Long userId;
        private Set<Long> roleIds;
        private Long organizationId; // Optionnel

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Set<Long> getRoleIds() {
            return roleIds;
        }

        public void setRoleIds(Set<Long> roleIds) {
            this.roleIds = roleIds;
        }

        public Long getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId) {
            this.organizationId = organizationId;
        }
    }
}

