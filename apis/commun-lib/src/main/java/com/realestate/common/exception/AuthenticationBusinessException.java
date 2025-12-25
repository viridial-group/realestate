package com.realestate.common.exception;

/**
 * Exception métier pour les erreurs d'authentification
 * HTTP Status: 401 UNAUTHORIZED
 */
public class AuthenticationBusinessException extends BusinessException {
    
    public AuthenticationBusinessException(String message) {
        super("AUTHENTICATION_FAILED", message);
    }
    
    public AuthenticationBusinessException(String message, Throwable cause) {
        super("AUTHENTICATION_FAILED", message, cause);
    }
}

