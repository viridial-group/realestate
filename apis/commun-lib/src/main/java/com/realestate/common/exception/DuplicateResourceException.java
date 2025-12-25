package com.realestate.common.exception;

/**
 * Exception levée lorsqu'une ressource existe déjà (duplication)
 * HTTP Status: 409 CONFLICT
 */
public class DuplicateResourceException extends BusinessException {
    
    private final String resourceType;
    private final String duplicateField;
    private final Object duplicateValue;
    
    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", message);
        this.resourceType = null;
        this.duplicateField = null;
        this.duplicateValue = null;
    }
    
    public DuplicateResourceException(String resourceType, String duplicateField, Object duplicateValue) {
        super("DUPLICATE_RESOURCE", 
              String.format("%s with %s '%s' already exists", resourceType, duplicateField, duplicateValue));
        this.resourceType = resourceType;
        this.duplicateField = duplicateField;
        this.duplicateValue = duplicateValue;
    }
    
    public DuplicateResourceException(String resourceType, String duplicateField, Object duplicateValue, String message) {
        super("DUPLICATE_RESOURCE", message);
        this.resourceType = resourceType;
        this.duplicateField = duplicateField;
        this.duplicateValue = duplicateValue;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public String getDuplicateField() {
        return duplicateField;
    }
    
    public Object getDuplicateValue() {
        return duplicateValue;
    }
}

