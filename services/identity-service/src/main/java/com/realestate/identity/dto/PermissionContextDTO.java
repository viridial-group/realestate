package com.realestate.identity.dto;

import java.util.Set;

/**
 * DTO pour le contexte de permissions d'un utilisateur
 */
public class PermissionContextDTO {
    private Long userId;
    private Set<String> roleNames;
    private boolean superAdmin;
    private boolean admin;
    private Set<Long> accessibleOrganizationIds; // Organisations + sous-organisations
    private Set<Long> directOrganizationIds; // Organisations directes seulement
    private String userType; // INDIVIDUAL, PROFESSIONAL, UNKNOWN

    public PermissionContextDTO() {
    }

    public PermissionContextDTO(
            Long userId,
            Set<String> roleNames,
            boolean superAdmin,
            boolean admin,
            Set<Long> accessibleOrganizationIds,
            Set<Long> directOrganizationIds,
            String userType) {
        this.userId = userId;
        this.roleNames = roleNames;
        this.superAdmin = superAdmin;
        this.admin = admin;
        this.accessibleOrganizationIds = accessibleOrganizationIds;
        this.directOrganizationIds = directOrganizationIds;
        this.userType = userType;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Set<String> roleNames) {
        this.roleNames = roleNames;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Set<Long> getAccessibleOrganizationIds() {
        return accessibleOrganizationIds;
    }

    public void setAccessibleOrganizationIds(Set<Long> accessibleOrganizationIds) {
        this.accessibleOrganizationIds = accessibleOrganizationIds;
    }

    public Set<Long> getDirectOrganizationIds() {
        return directOrganizationIds;
    }

    public void setDirectOrganizationIds(Set<Long> directOrganizationIds) {
        this.directOrganizationIds = directOrganizationIds;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}

