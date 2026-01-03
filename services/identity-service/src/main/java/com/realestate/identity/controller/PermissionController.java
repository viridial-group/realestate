package com.realestate.identity.controller;

import com.realestate.identity.dto.PermissionDTO;
import com.realestate.identity.entity.Permission;
import com.realestate.identity.mapper.PermissionMapper;
import com.realestate.identity.service.PermissionService;
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
@RequestMapping("/api/identity/permissions")
@Tag(name = "Permissions", description = "Permission management API")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    public PermissionController(PermissionService permissionService, PermissionMapper permissionMapper) {
        this.permissionService = permissionService;
        this.permissionMapper = permissionMapper;
    }

    @GetMapping
    @Operation(summary = "List all permissions", description = "Returns a list of all permissions in the system")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        List<PermissionDTO> permissionDTOs = permissions.stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID", description = "Returns permission information for a specific permission ID")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getPermissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));
        return ResponseEntity.ok(permissionMapper.toDTO(permission));
    }

    @GetMapping("/resource/{resource}")
    @Operation(summary = "Get permissions by resource", description = "Returns all permissions for a specific resource")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByResource(@PathVariable String resource) {
        List<Permission> permissions = permissionService.getPermissionsByResource(resource);
        List<PermissionDTO> permissionDTOs = permissions.stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionDTOs);
    }

    @PostMapping
    @Operation(summary = "Create permission", description = "Creates a new permission")
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        Permission permission = permissionMapper.toEntity(permissionDTO);
        Permission created = permissionService.createPermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update permission", description = "Updates permission information")
    public ResponseEntity<PermissionDTO> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionDTO permissionDTO) {
        Permission permissionDetails = permissionMapper.toEntity(permissionDTO);
        Permission updated = permissionService.updatePermission(id, permissionDetails);
        return ResponseEntity.ok(permissionMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission", description = "Deletes a permission from the database")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

