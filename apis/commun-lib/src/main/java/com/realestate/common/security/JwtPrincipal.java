package com.realestate.common.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Représente le principal utilisateur authentifié extrait du JWT
 * Utilisé par tous les microservices
 */
public class JwtPrincipal implements UserDetails {

    private UUID userId;
    private UUID organizationId;
    private List<UUID> accessibleTenants;
    private Set<String> roles;
    private Set<String> permissions;
    private String preferredLanguage; // Langue préférée de l'utilisateur (ex: "fr", "en", "es")

    // Constructeurs
    public JwtPrincipal() {
    }

    public JwtPrincipal(UUID userId, UUID organizationId, List<UUID> accessibleTenants, 
                        Set<String> roles, Set<String> permissions) {
        this(userId, organizationId, accessibleTenants, roles, permissions, null);
    }

    public JwtPrincipal(UUID userId, UUID organizationId, List<UUID> accessibleTenants, 
                        Set<String> roles, Set<String> permissions, String preferredLanguage) {
        this.userId = userId;
        this.organizationId = organizationId;
        this.accessibleTenants = accessibleTenants;
        this.roles = roles;
        this.permissions = permissions;
        this.preferredLanguage = preferredLanguage;
    }

    // Getters
    public UUID getUserId() {
        return userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public List<UUID> getAccessibleTenants() {
        return accessibleTenants;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    // Setters
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public void setAccessibleTenants(List<UUID> accessibleTenants) {
        this.accessibleTenants = accessibleTenants;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions != null ? permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList() : List.of();
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return userId != null ? userId.toString() : null; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
