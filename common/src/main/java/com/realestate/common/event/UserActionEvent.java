package com.realestate.common.event;

/**
 * Generic event for user actions that need to be audited.
 */
public class UserActionEvent extends BaseEvent {
    
    private String action; // CREATE, UPDATE, DELETE, VIEW, etc.
    private String resourceType; // PROPERTY, RESOURCE, DOCUMENT, etc.
    private Long resourceId;
    private String resourceName;
    private String details; // JSON string with additional details
    
    public UserActionEvent() {
        super();
    }
    
    public UserActionEvent(Long organizationId, Long userId, String action, 
                          String resourceType, Long resourceId, String resourceName, String details) {
        super(organizationId, userId);
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.details = details;
    }
    
    // Getters and Setters
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    
    public Long getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
}

