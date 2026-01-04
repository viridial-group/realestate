package com.realestate.identity.dto;

/**
 * Réponse après l'inscription avec abonnement
 */
public class SubscribeResponse {

    private AuthResponse auth;
    private Long organizationId;
    private Long subscriptionId;
    private String organizationName;
    private String planName;

    public SubscribeResponse() {
    }

    public SubscribeResponse(AuthResponse auth, Long organizationId, Long subscriptionId, String organizationName, String planName) {
        this.auth = auth;
        this.organizationId = organizationId;
        this.subscriptionId = subscriptionId;
        this.organizationName = organizationName;
        this.planName = planName;
    }

    public AuthResponse getAuth() {
        return auth;
    }

    public void setAuth(AuthResponse auth) {
        this.auth = auth;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }
}

