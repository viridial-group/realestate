package com.realestate.common.exception;

/**
 * Exception métier pour les erreurs d'accès refusé
 * HTTP Status: 403 FORBIDDEN
 */
public class AccessDeniedBusinessException extends BusinessException {
    
    public AccessDeniedBusinessException(String message) {
        super("ACCESS_DENIED", message);
    }
    
    public AccessDeniedBusinessException(String resourceType, Object resourceId) {
        super("ACCESS_DENIED", 
              String.format("Access denied to %s with id: %s", resourceType, resourceId));
    }
}

