package com.realestate.identity.service;

import com.realestate.identity.entity.Permission;
import com.realestate.identity.entity.Role;
import com.realestate.identity.entity.User;
import com.realestate.identity.repository.PermissionRepository;
import com.realestate.identity.repository.RoleRepository;
import com.realestate.identity.repository.UserRepository;
import com.realestate.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public RoleService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<Role> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Role> getAllRoles(String search, Boolean isSystem, Pageable pageable) {
        Specification<Role> spec = Specification.where(null);

        if (search != null && !search.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                    )
            );
        }

        // Note: isSystem n'est pas dans l'entité Role actuellement
        // Si vous voulez filtrer par système, vous devrez ajouter ce champ à l'entité

        return roleRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRoleByIdWithPermissions(Long id) {
        return roleRepository.findById(id)
                .map(role -> {
                    // Force le chargement des permissions et utilisateurs
                    role.getPermissions().size();
                    role.getUsers().size();
                    return role;
                });
    }

    @Transactional
    public Role createRole(Role role, Set<Long> permissionIds) {
        // Vérifier si le nom existe déjà
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role with name " + role.getName() + " already exists");
        }

        // Charger les permissions
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> permissions = permissionIds.stream()
                    .map(id -> permissionRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Permission", id)))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, Role roleDetails, Set<Long> permissionIds) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        // Vérifier si le nom change et s'il existe déjà
        if (roleDetails.getName() != null && !roleDetails.getName().equals(role.getName())) {
            if (roleRepository.existsByName(roleDetails.getName())) {
                throw new IllegalArgumentException("Role with name " + roleDetails.getName() + " already exists");
            }
            role.setName(roleDetails.getName());
        }

        if (roleDetails.getDescription() != null) {
            role.setDescription(roleDetails.getDescription());
        }

        // Mettre à jour les permissions
        if (permissionIds != null) {
            Set<Permission> permissions = permissionIds.stream()
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new ResourceNotFoundException("Permission", permissionId)))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        // Vérifier si le rôle est utilisé par des utilisateurs
        // Note: On vérifie via la relation inverse dans Role
        if (!role.getUsers().isEmpty()) {
            throw new IllegalStateException("Cannot delete role. It is assigned to " + role.getUsers().size() + " user(s)");
        }

        roleRepository.delete(role);
    }

    @Transactional
    public void assignRolesToUser(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Charger les rôles avec leurs permissions
        Set<Role> roles = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", roleId)))
                .collect(Collectors.toSet());

        // Gérer la relation bidirectionnelle
        // Retirer l'utilisateur des anciens rôles
        user.getRoles().forEach(role -> role.getUsers().remove(user));
        
        // Ajouter l'utilisateur aux nouveaux rôles
        roles.forEach(role -> role.getUsers().add(user));
        
        // Mettre à jour les rôles de l'utilisateur
        user.setRoles(roles);
        userRepository.save(user);
    }

    /**
     * Assign a role to a user by role name
     * Useful for assigning system roles like ORGANIZATION_ADMIN
     */
    @Transactional
    public void assignRoleToUserByName(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role with name: " + roleName));

        // Add role if not already assigned
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            role.getUsers().add(user);
            userRepository.save(user);
        }
    }

    @Transactional(readOnly = true)
    public long countRoles() {
        return roleRepository.count();
    }

    @Transactional(readOnly = true)
    public long countSystemRoles() {
        // Note: Si vous ajoutez un champ isSystem à Role, utilisez-le ici
        // Pour l'instant, on considère que les rôles ADMIN, USER, MANAGER sont système
        return roleRepository.findAll().stream()
                .filter(role -> {
                    String name = role.getName();
                    return "ADMIN".equals(name) || "USER".equals(name) || "MANAGER".equals(name);
                })
                .count();
    }
}

