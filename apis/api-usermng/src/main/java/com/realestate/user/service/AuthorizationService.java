package com.realestate.user.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.realestate.user.permission.entity.Permission;
import com.realestate.user.permission.repository.PermissionRepository;
import com.realestate.user.role.entity.Role;
import com.realestate.user.role.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * Calcule les permissions effectives d'un ensemble de rôles
     * Optimisé pour éviter les problèmes de lazy loading
     */
    public Set<String> computePermissions(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        
        Set<Permission> permissions = new HashSet<>();
        Set<UUID> processedRoleIds = new HashSet<>(); // Éviter les cycles

        for (Role role : roles) {
            collect(role, permissions, processedRoleIds);
        }

        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    /**
     * Collecte récursive des permissions avec protection contre les cycles
     */
    private void collect(Role role, Set<Permission> permissions, Set<UUID> processedRoleIds) {
        if (role == null || processedRoleIds.contains(role.getId())) {
            return; // Éviter les cycles et les rôles déjà traités
        }
        
        processedRoleIds.add(role.getId());
        
        // Charger les permissions du rôle (batch fetch devrait les charger)
        if (role.getPermissions() != null) {
            permissions.addAll(role.getPermissions());
        }
        
        // Traiter le rôle parent récursivement
        if (role.getParentRole() != null) {
            collect(role.getParentRole(), permissions, processedRoleIds);
        }
    }
}
