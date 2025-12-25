package com.realestate.common.security;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.realestate.common.service.CurrentUserService;

/**
 * Service utilitaire pour vérifier les rôles et organisations
 * Utilisez les méthodes de ce service dans vos controllers ou services
 */
@Component
public class RoleAndOrganizationFilter {

    private final CurrentUserService currentUserService;

    public RoleAndOrganizationFilter(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    /**
     * Vérifie si l'utilisateur a au moins un des rôles requis
     */
    public void requireAnyRole(String... roles) {
        var userRoles = currentUserService.getCurrentUserRoles();
        boolean hasAnyRole = Arrays.stream(roles)
                .anyMatch(userRoles::contains);
        if (!hasAnyRole) {
            throw new AccessDeniedException(
                    "Accès refusé : au moins un de ces rôles est requis : " + Arrays.toString(roles));
        }
    }

    /**
     * Vérifie si l'utilisateur a tous les rôles requis
     */
    public void requireAllRoles(String... roles) {
        var userRoles = currentUserService.getCurrentUserRoles();
        boolean hasAllRoles = Arrays.stream(roles)
                .allMatch(userRoles::contains);
        if (!hasAllRoles) {
            throw new AccessDeniedException(
                    "Accès refusé : tous ces rôles sont requis : " + Arrays.toString(roles));
        }
    }

    /**
     * Vérifie si l'utilisateur a une permission spécifique
     */
    public void requirePermission(String permission) {
        if (!currentUserService.hasPermission(permission)) {
            throw new AccessDeniedException(
                    "Accès refusé : permission requise : " + permission);
        }
    }

    /**
     * Vérifie si l'utilisateur peut accéder à l'organisation (inclut les enfants)
     */
    public void requireOrganizationAccess(UUID organizationId, boolean allowChildren) {
        if (allowChildren) {
            if (!currentUserService.canAccessOrganization(organizationId)) {
                throw new AccessDeniedException(
                        "Accès refusé : vous n'avez pas accès à l'organisation " + organizationId);
            }
        } else {
            UUID userOrgId = currentUserService.getCurrentOrganizationId();
            if (!userOrgId.equals(organizationId)) {
                throw new AccessDeniedException(
                        "Accès refusé : vous devez appartenir à l'organisation " + organizationId);
            }
        }
    }
}

