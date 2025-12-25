package com.realestate.common.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import com.realestate.common.i18n.I18nService;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Gestionnaire global des exceptions pour tous les microservices
 * Capture toutes les exceptions non gérées et retourne des réponses HTTP appropriées
 * Utilise l'internationalisation (i18n) pour les messages
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final I18nService i18nService;
    
    public GlobalExceptionHandler(I18nService i18nService) {
        this.i18nService = i18nService;
    }

    /**
     * Gère les exceptions de validation (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errorId = UUID.randomUUID().toString();
        logger.warn("Validation error [{}]: {} - Fields: {}", errorId, ex.getMessage(), errors, ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message(i18nService.getMessage("error.validation"))
                .details(errors.toString())
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Gère les violations de contraintes (ConstraintViolationException)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }

        String errorId = UUID.randomUUID().toString();
        logger.warn("Constraint violation [{}]: {} - Violations: {}", errorId, ex.getMessage(), errors, ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("CONSTRAINT_VIOLATION")
                .message(i18nService.getMessage("error.validation"))
                .details(errors.toString())
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Gère les exceptions d'entité non trouvée
     */
    @ExceptionHandler({EntityNotFoundException.class, jakarta.persistence.EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            Exception ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Entity not found [{}]: {}", errorId, ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("ENTITY_NOT_FOUND")
                .message(ex.getMessage() != null ? ex.getMessage() : i18nService.getMessage("error.not.found"))
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Gère les exceptions d'accès refusé (Spring Security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Access denied [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("ACCESS_DENIED")
                .message(i18nService.getMessage("error.access.denied") + 
                        (ex.getMessage() != null ? " : " + ex.getMessage() : ""))
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Gère les exceptions d'authentification
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Authentication failed [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("AUTHENTICATION_FAILED")
                .message(i18nService.getMessage("error.authentication.failed") + 
                        (ex.getMessage() != null ? " : " + ex.getMessage() : ""))
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Gère les exceptions de verrouillage optimiste (concurrence)
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(
            ObjectOptimisticLockingFailureException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        String entityInfo = ex.getClass().getSimpleName();
        Object identifier = ex.getIdentifier() != null ? ex.getIdentifier() : "Unknown";
        
        logger.warn("Concurrent update detected [{}]: Entity={}, Id={}, Path={}", 
                errorId, entityInfo, identifier, getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("CONCURRENT_UPDATE")
                .message(i18nService.getMessage("error.concurrent.update"))
                .details(String.format("Entity: %s, Id: %s", entityInfo, identifier))
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Gère les exceptions ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Resource not found [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .details(ex.getResourceType() != null ? 
                    String.format("ResourceType: %s, ResourceId: %s", ex.getResourceType(), ex.getResourceId()) : null)
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Gère les exceptions BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Business exception [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ex.getErrorCode())
                .message(ex.getMessage())
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Gère les exceptions ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Validation exception [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message(ex.getMessage())
                .details(ex.getField() != null ? String.format("Field: %s", ex.getField()) : null)
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Gère les exceptions DuplicateResourceException
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Duplicate resource [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("DUPLICATE_RESOURCE")
                .message(ex.getMessage())
                .details(ex.getResourceType() != null ? 
                    String.format("ResourceType: %s, Field: %s, Value: %s", 
                        ex.getResourceType(), ex.getDuplicateField(), ex.getDuplicateValue()) : null)
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Gère les exceptions AccessDeniedBusinessException
     */
    @ExceptionHandler(AccessDeniedBusinessException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedBusinessException(
            AccessDeniedBusinessException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Access denied (business) [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("ACCESS_DENIED")
                .message(ex.getMessage())
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Gère les exceptions AuthenticationBusinessException
     */
    @ExceptionHandler(AuthenticationBusinessException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationBusinessException(
            AuthenticationBusinessException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Authentication failed (business) [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("AUTHENTICATION_FAILED")
                .message(ex.getMessage())
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Gère les exceptions OptimisticLockException
     */
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(
            OptimisticLockException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Optimistic lock exception [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("CONCURRENT_UPDATE")
                .message(ex.getMessage())
                .details(ex.getEntityType() != null ? 
                    String.format("EntityType: %s, EntityId: %s", ex.getEntityType(), ex.getEntityId()) : null)
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Gère les exceptions IllegalArgumentException (erreurs métier)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Illegal argument [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("ILLEGAL_ARGUMENT")
                .message(ex.getMessage())
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Gère les exceptions RuntimeException (erreurs métier génériques)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.error("Runtime exception [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("RUNTIME_ERROR")
                .message(ex.getMessage() != null ? ex.getMessage() : i18nService.getMessage("error.generic"))
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Gère toutes les autres exceptions non gérées
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.error("Unexpected exception [{}]: {} - Path: {}", errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message(i18nService.getMessage("error.generic"))
                .details(ex.getClass().getName() + ": " + ex.getMessage())
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Extrait le chemin de la requête
     */
    protected String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /**
     * Classe pour la réponse d'erreur standardisée
     */
    @Data
    @NoArgsConstructor
    public static class ErrorResponse {
        private String errorId;
        private LocalDateTime timestamp;
        private Integer status;
        private String error;
        private String message;
        private String details;
        private String path;
        
        public ErrorResponse(String errorId, LocalDateTime timestamp, Integer status, String error, 
                           String message, String details, String path) {
            this.errorId = errorId;
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.details = details;
            this.path = path;
        }
        
        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }
        
        public static class ErrorResponseBuilder {
            private String errorId;
            private LocalDateTime timestamp;
            private Integer status;
            private String error;
            private String message;
            private String details;
            private String path;
            
            public ErrorResponseBuilder errorId(String errorId) {
                this.errorId = errorId;
                return this;
            }
            
            public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }
            
            public ErrorResponseBuilder status(Integer status) {
                this.status = status;
                return this;
            }
            
            public ErrorResponseBuilder error(String error) {
                this.error = error;
                return this;
            }
            
            public ErrorResponseBuilder message(String message) {
                this.message = message;
                return this;
            }
            
            public ErrorResponseBuilder details(String details) {
                this.details = details;
                return this;
            }
            
            public ErrorResponseBuilder path(String path) {
                this.path = path;
                return this;
            }
            
            public ErrorResponse build() {
                return new ErrorResponse(errorId, timestamp, status, error, message, details, path);
            }
        }
    }
}

