package com.realestate.common.exception;

/**
 * Exception pour les erreurs de validation
 * HTTP Status: 400 BAD_REQUEST
 */
public class ValidationException extends RuntimeException {
    
    private final String field;
    
    public ValidationException(String message) {
        super(message);
        this.field = null;
    }
    
    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
    }
    
    public String getField() {
        return field;
    }
}

