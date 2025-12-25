package com.realestate.common.exception;

/**
 * Exception pour les conflits de verrouillage optimiste (accès concurrent)
 * HTTP Status: 409 CONFLICT
 */
public class OptimisticLockException extends BusinessException {
    
    private final String entityType;
    private final Object entityId;
    
    public OptimisticLockException(String message) {
        super("CONCURRENT_UPDATE", message);
        this.entityType = null;
        this.entityId = null;
    }
    
    public OptimisticLockException(String entityType, Object entityId) {
        super("CONCURRENT_UPDATE", 
              String.format("Concurrent update detected for %s with id: %s", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public OptimisticLockException(String entityType, Object entityId, String message) {
        super("CONCURRENT_UPDATE", message);
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public Object getEntityId() {
        return entityId;
    }
}

