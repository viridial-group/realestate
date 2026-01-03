package com.realestate.identity.service;

import com.realestate.identity.entity.Permission;
import com.realestate.identity.repository.PermissionRepository;
import com.realestate.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionByName(String name) {
        return permissionRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByResource(String resource) {
        return permissionRepository.findByResource(resource);
    }

    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByAction(String action) {
        return permissionRepository.findByAction(action);
    }

    @Transactional
    public Permission createPermission(Permission permission) {
        // Vérifier si le nom existe déjà
        if (permissionRepository.existsByName(permission.getName())) {
            throw new IllegalArgumentException("Permission with name " + permission.getName() + " already exists");
        }

        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission updatePermission(Long id, Permission permissionDetails) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));

        // Vérifier si le nom change et s'il existe déjà
        if (permissionDetails.getName() != null && !permissionDetails.getName().equals(permission.getName())) {
            if (permissionRepository.existsByName(permissionDetails.getName())) {
                throw new IllegalArgumentException("Permission with name " + permissionDetails.getName() + " already exists");
            }
            permission.setName(permissionDetails.getName());
        }

        if (permissionDetails.getResource() != null) {
            permission.setResource(permissionDetails.getResource());
        }

        if (permissionDetails.getAction() != null) {
            permission.setAction(permissionDetails.getAction());
        }

        if (permissionDetails.getDescription() != null) {
            permission.setDescription(permissionDetails.getDescription());
        }

        return permissionRepository.save(permission);
    }

    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));

        // Vérifier si la permission est utilisée par des rôles
        if (!permission.getRoles().isEmpty()) {
            throw new IllegalStateException("Cannot delete permission. It is assigned to " + permission.getRoles().size() + " role(s)");
        }

        permissionRepository.delete(permission);
    }
}

