package com.realestate.identity.dto;

import java.time.LocalDateTime;

public class OrganizationUserDTO {
    private Long id;
    private Long userId;
    private Long organizationId;
    private String organizationName;
    private Long teamId;
    private Boolean active;
    private Boolean isPrimary;
    private String customRoles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User information
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String userName; // Computed: firstName + lastName or email

    public OrganizationUserDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getCustomRoles() {
        return customRoles;
    }

    public void setCustomRoles(String customRoles) {
        this.customRoles = customRoles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserName() {
        if (userName == null || userName.isEmpty()) {
            if (userFirstName != null && userLastName != null) {
                userName = (userFirstName + " " + userLastName).trim();
            } else if (userFirstName != null && !userFirstName.isEmpty()) {
                userName = userFirstName;
            } else if (userLastName != null && !userLastName.isEmpty()) {
                userName = userLastName;
            } else if (userEmail != null && !userEmail.isEmpty()) {
                userName = userEmail;
            } else {
                userName = "Utilisateur " + userId;
            }
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

