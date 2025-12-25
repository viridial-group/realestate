package com.realestate.common.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.realestate.common.exception.AuthenticationBusinessException;
import com.realestate.common.security.JwtPrincipal;

import lombok.extern.slf4j.Slf4j;

/**
 * Service pour obtenir les informations de l'utilisateur connecté depuis le JWT
 * Utilisé par tous les microservices
 */
@Slf4j
@Service
public class CurrentUserService {

    /**
     * Récupère l'ID de l'utilisateur connecté depuis le JWT
     * Retourne null si l'utilisateur n'est pas authentifié (vue anonyme)
     */
    public UUID getCurrentUserId() {
        JwtPrincipal principal = getPrincipal();
        return principal != null ? principal.getUserId() : null;
    }

    /**
     * Récupère l'ID de l'organisation de l'utilisateur connecté depuis le JWT
     * Lance une exception si l'utilisateur n'est pas authentifié
     */
    public UUID getCurrentOrganizationId() {
        JwtPrincipal principal = getPrincipal();
        if (principal == null) {
            throw new AuthenticationBusinessException("User not authenticated");
        }
        return principal.getOrganizationId();
    }

    /**
     * Récupère la liste des IDs d'organisations accessibles par l'utilisateur connecté
     * Inclut l'organisation de l'utilisateur et toutes ses organisations enfants
     */
    public List<UUID> getAccessibleOrganizationIds() {
        JwtPrincipal principal = getPrincipal();
        return principal != null ? principal.getAccessibleTenants() : List.of();
    }

    /**
     * Récupère les rôles de l'utilisateur connecté
     */
    public java.util.Set<String> getCurrentUserRoles() {
        JwtPrincipal principal = getPrincipal();
        return principal != null ? principal.getRoles() : java.util.Set.of();
    }

    /**
     * Récupère les permissions de l'utilisateur connecté
     */
    public java.util.Set<String> getCurrentUserPermissions() {
        JwtPrincipal principal = getPrincipal();
        return principal != null ? principal.getPermissions() : java.util.Set.of();
    }

    /**
     * Vérifie si l'utilisateur a une permission spécifique
     */
    public boolean hasPermission(String permission) {
        return getCurrentUserPermissions().contains(permission);
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    /**
     * Vérifie si l'utilisateur peut accéder à une organisation (inclut les enfants)
     */
    public boolean canAccessOrganization(UUID organizationId) {
        List<UUID> accessibleOrgs = getAccessibleOrganizationIds();
        return accessibleOrgs.contains(organizationId);
    }

    /**
     * Récupère le JwtPrincipal de l'utilisateur connecté
     */
    public JwtPrincipal getCurrentPrincipal() {
        return getPrincipal();
    }

    /**
     * Extrait le JwtPrincipal du SecurityContext
     */
    private JwtPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtPrincipal) {
            return (JwtPrincipal) authentication.getPrincipal();
        }
        return null;
    }
}

