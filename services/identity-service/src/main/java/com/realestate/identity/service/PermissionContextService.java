package com.realestate.identity.service;

import com.realestate.identity.entity.Organization;
import com.realestate.identity.entity.OrganizationUser;
import com.realestate.identity.entity.Role;
import com.realestate.identity.entity.User;
import com.realestate.identity.repository.OrganizationRepository;
import com.realestate.identity.repository.OrganizationUserRepository;
import com.realestate.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour gérer le contexte de permissions d'un utilisateur
 * Fournit les informations nécessaires pour filtrer les données selon les rôles et organisations
 */
@Service
public class PermissionContextService {

    private final UserRepository userRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final OrganizationRepository organizationRepository;

    public PermissionContextService(
            UserRepository userRepository,
            OrganizationUserRepository organizationUserRepository,
            OrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.organizationUserRepository = organizationUserRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Contexte de permissions pour un utilisateur
     */
    public static class PermissionContext {
        private final Long userId;
        private final Set<String> roleNames;
        private final boolean isSuperAdmin;
        private final boolean isAdmin;
        private final Set<Long> accessibleOrganizationIds; // Organisations + sous-organisations
        private final Set<Long> directOrganizationIds; // Organisations directes seulement
        private final UserTypeService.UserType userType;

        public PermissionContext(
                Long userId,
                Set<String> roleNames,
                boolean isSuperAdmin,
                boolean isAdmin,
                Set<Long> accessibleOrganizationIds,
                Set<Long> directOrganizationIds,
                UserTypeService.UserType userType) {
            this.userId = userId;
            this.roleNames = roleNames;
            this.isSuperAdmin = isSuperAdmin;
            this.isAdmin = isAdmin;
            this.accessibleOrganizationIds = accessibleOrganizationIds;
            this.directOrganizationIds = directOrganizationIds;
            this.userType = userType;
        }

        public Long getUserId() {
            return userId;
        }

        public Set<String> getRoleNames() {
            return roleNames;
        }

        public boolean isSuperAdmin() {
            return isSuperAdmin;
        }

        public boolean isAdmin() {
            return isAdmin;
        }

        public Set<Long> getAccessibleOrganizationIds() {
            return accessibleOrganizationIds;
        }

        public Set<Long> getDirectOrganizationIds() {
            return directOrganizationIds;
        }

        public UserTypeService.UserType getUserType() {
            return userType;
        }

        /**
         * Vérifie si l'utilisateur peut accéder à une organisation
         */
        public boolean canAccessOrganization(Long organizationId) {
            if (isSuperAdmin || isAdmin) {
                return true; // Super admin et admin voient tout
            }
            return accessibleOrganizationIds.contains(organizationId);
        }

        /**
         * Vérifie si l'utilisateur peut accéder à une propriété
         */
        public boolean canAccessProperty(Long propertyCreatedBy, Long propertyOrganizationId) {
            if (isSuperAdmin || isAdmin) {
                return true; // Super admin et admin voient tout
            }

            if (userType == UserTypeService.UserType.INDIVIDUAL) {
                // Individuel : seulement ses propres propriétés
                return propertyCreatedBy != null && propertyCreatedBy.equals(userId);
            } else if (userType == UserTypeService.UserType.PROFESSIONAL) {
                // Professionnel : propriétés de son organisation et sous-organisations
                return propertyOrganizationId != null && accessibleOrganizationIds.contains(propertyOrganizationId);
            }

            return false;
        }
    }

    /**
     * Récupère le contexte de permissions pour un utilisateur
     */
    @Transactional(readOnly = true)
    public PermissionContext getPermissionContext(Long userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        boolean isSuperAdmin = roleNames.contains("SUPER_ADMIN");
        boolean isAdmin = roleNames.contains("ADMIN");

        // Déterminer le type d'utilisateur
        UserTypeService.UserType userType = determineUserType(user, roleNames);

        // Récupérer les organisations accessibles
        Set<Long> directOrganizationIds = new HashSet<>();
        Set<Long> accessibleOrganizationIds = new HashSet<>();

        if (!isSuperAdmin && !isAdmin) {
            // Récupérer les organisations directes de l'utilisateur
            List<OrganizationUser> orgUsers = organizationUserRepository.findByUserId(userId);
            directOrganizationIds = orgUsers.stream()
                    .map(OrganizationUser::getOrganization)
                    .map(Organization::getId)
                    .collect(Collectors.toSet());

            // Récupérer toutes les sous-organisations récursivement
            for (Long orgId : directOrganizationIds) {
                accessibleOrganizationIds.add(orgId);
                accessibleOrganizationIds.addAll(getAllSubOrganizationIds(orgId));
            }
        } else {
            // Super admin et admin : accès à toutes les organisations
            // On peut laisser accessibleOrganizationIds vide pour indiquer "toutes"
            // ou charger toutes les organisations si nécessaire
        }

        return new PermissionContext(
                userId,
                roleNames,
                isSuperAdmin,
                isAdmin,
                accessibleOrganizationIds,
                directOrganizationIds,
                userType
        );
    }

    /**
     * Récupère le contexte de permissions pour un utilisateur par email
     */
    @Transactional(readOnly = true)
    public PermissionContext getPermissionContextByEmail(String email) {
        User user = userRepository.findByEmailWithRolesAndPermissions(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return getPermissionContext(user.getId());
    }

    /**
     * Récupère récursivement tous les IDs des sous-organisations
     */
    @Transactional(readOnly = true)
    private Set<Long> getAllSubOrganizationIds(Long organizationId) {
        Set<Long> allSubOrgIds = new HashSet<>();
        collectSubOrganizationIds(organizationId, allSubOrgIds);
        return allSubOrgIds;
    }

    /**
     * Collecte récursivement les IDs des sous-organisations
     */
    private void collectSubOrganizationIds(Long parentId, Set<Long> collectedIds) {
        List<Organization> children = organizationRepository.findByParentId(parentId);
        for (Organization child : children) {
            collectedIds.add(child.getId());
            // Récursion pour les sous-sous-organisations
            collectSubOrganizationIds(child.getId(), collectedIds);
        }
    }

    /**
     * Détermine le type d'utilisateur
     */
    private UserTypeService.UserType determineUserType(User user, Set<String> roleNames) {
        boolean hasOrganization = !organizationUserRepository.findByUserId(user.getId()).isEmpty();

        if (hasOrganization || roleNames.contains("ORGANIZATION_ADMIN") ||
            roleNames.contains("MANAGER") || roleNames.contains("USER")) {
            return UserTypeService.UserType.PROFESSIONAL;
        } else if (roleNames.contains("INDIVIDUAL")) {
            return UserTypeService.UserType.INDIVIDUAL;
        }

        return hasOrganization ? UserTypeService.UserType.PROFESSIONAL : UserTypeService.UserType.INDIVIDUAL;
    }

    /**
     * Vérifie si un utilisateur peut accéder à une organisation
     */
    @Transactional(readOnly = true)
    public boolean canAccessOrganization(Long userId, Long organizationId) {
        PermissionContext context = getPermissionContext(userId);
        return context.canAccessOrganization(organizationId);
    }

    /**
     * Vérifie si un utilisateur peut accéder à une propriété
     */
    @Transactional(readOnly = true)
    public boolean canAccessProperty(Long userId, Long propertyCreatedBy, Long propertyOrganizationId) {
        PermissionContext context = getPermissionContext(userId);
        return context.canAccessProperty(propertyCreatedBy, propertyOrganizationId);
    }

    /**
     * Filtre une liste d'IDs d'organisations selon les permissions
     * Retourne seulement les IDs accessibles par l'utilisateur
     */
    @Transactional(readOnly = true)
    public Set<Long> filterAccessibleOrganizationIds(Long userId, Set<Long> organizationIds) {
        PermissionContext context = getPermissionContext(userId);
        
        if (context.isSuperAdmin() || context.isAdmin()) {
            // Super admin et admin : retourner tous les IDs
            return organizationIds;
        }

        // Filtrer selon les organisations accessibles
        return organizationIds.stream()
                .filter(context::canAccessOrganization)
                .collect(Collectors.toSet());
    }
}

