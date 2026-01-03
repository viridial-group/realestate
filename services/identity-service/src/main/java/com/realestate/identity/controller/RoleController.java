package com.realestate.identity.controller;

import com.realestate.identity.dto.RoleDTO;
import com.realestate.identity.entity.Role;
import com.realestate.identity.mapper.RoleMapper;
import com.realestate.identity.service.RoleService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/identity/roles")
@Tag(name = "Roles", description = "Role management API")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @GetMapping
    @Operation(summary = "List all roles", description = "Returns a paginated list of all roles with optional filters")
    public ResponseEntity<Page<RoleDTO>> getAllRoles(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isSystem,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> rolesPage = roleService.getAllRoles(search, isSystem, pageable);
        Page<RoleDTO> roleDTOsPage = rolesPage.map(role -> {
            // Charger les permissions et utilisateurs pour chaque rôle
            if (role.getPermissions() != null) {
                role.getPermissions().size(); // Force le chargement des permissions
            }
            if (role.getUsers() != null) {
                role.getUsers().size(); // Force le chargement des utilisateurs
            }
            return roleMapper.toDTO(role);
        });
        return ResponseEntity.ok(roleDTOsPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Returns role information for a specific role ID")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleByIdWithPermissions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        // Charger les utilisateurs aussi
        if (role.getUsers() != null) {
            role.getUsers().size(); // Force le chargement
        }
        return ResponseEntity.ok(roleMapper.toDTO(role));
    }

    @PostMapping
    @Operation(summary = "Create role", description = "Creates a new role with optional permissions")
    public ResponseEntity<RoleDTO> createRole(
            @Valid @RequestBody RoleCreateRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());

        Role created = roleService.createRole(role, request.getPermissionIds());
        // Charger les permissions pour le DTO
        if (created.getPermissions() != null) {
            created.getPermissions().size();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(roleMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role", description = "Updates role information and permissions")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {
        Role roleDetails = new Role();
        roleDetails.setName(request.getName());
        roleDetails.setDescription(request.getDescription());

        Role updated = roleService.updateRole(id, roleDetails, request.getPermissionIds());
        // Charger les permissions et utilisateurs pour le DTO
        if (updated.getPermissions() != null) {
            updated.getPermissions().size();
        }
        if (updated.getUsers() != null) {
            updated.getUsers().size();
        }
        return ResponseEntity.ok(roleMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Deletes a role from the database")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get role statistics", description = "Returns statistics about roles")
    public ResponseEntity<Map<String, Object>> getRoleStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRoles", roleService.countRoles());
        stats.put("systemRoles", roleService.countSystemRoles());
        stats.put("customRoles", roleService.countRoles() - roleService.countSystemRoles());
        return ResponseEntity.ok(stats);
    }

    // DTOs pour les requêtes
    public static class RoleCreateRequest {
        private String name;
        private String description;
        private Set<Long> permissionIds;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Set<Long> getPermissionIds() {
            return permissionIds;
        }

        public void setPermissionIds(Set<Long> permissionIds) {
            this.permissionIds = permissionIds;
        }
    }

    public static class RoleUpdateRequest {
        private String name;
        private String description;
        private Set<Long> permissionIds;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Set<Long> getPermissionIds() {
            return permissionIds;
        }

        public void setPermissionIds(Set<Long> permissionIds) {
            this.permissionIds = permissionIds;
        }
    }
}

