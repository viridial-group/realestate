package com.realestate.common.exception;

/**
 * Exception levée lorsqu'une ressource n'est pas trouvée
 * HTTP Status: 404 NOT_FOUND
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceType;
    private final Object resourceId;
    
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = null;
        this.resourceId = null;
    }
    
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s not found with id: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    public ResourceNotFoundException(String resourceType, Object resourceId, String message) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public Object getResourceId() {
        return resourceId;
    }
}

