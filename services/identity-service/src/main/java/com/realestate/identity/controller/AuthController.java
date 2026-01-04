package com.realestate.identity.controller;

import com.realestate.common.exception.BadRequestException;
import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.identity.dto.AuthResponse;
import com.realestate.identity.dto.LoginRequest;
import com.realestate.identity.dto.RefreshTokenRequest;
import com.realestate.identity.dto.RegisterRequest;
import com.realestate.identity.dto.SubscribeRequest;
import com.realestate.identity.dto.SubscribeResponse;
import com.realestate.identity.service.AuthService;
import com.realestate.identity.service.SubscribeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identity/auth")
@Tag(name = "Authentication", description = "Authentication API for user registration, login, token refresh, and logout")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final SubscribeService subscribeService;

    public AuthController(AuthService authService, SubscribeService subscribeService) {
        this.authService = authService;
        this.subscribeService = subscribeService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns JWT access and refresh tokens")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user with email and password, returns JWT access and refresh tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates a new access token from a valid refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates the user's JWT token by adding it to the blacklist")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.substring(7); // Remove "Bearer " prefix
            authService.logout(token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/subscribe")
    @Operation(
            summary = "Subscribe with organization creation",
            description = "Creates a new user, organization, assigns user as organization admin, and creates a subscription in one transaction"
    )
    public ResponseEntity<SubscribeResponse> subscribe(@Valid @RequestBody SubscribeRequest request) {
        try {
            SubscribeResponse response = subscribeService.subscribe(request);
            logger.info("Subscription successful for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BadRequestException e) {
            logger.warn("Bad request for subscription: {}", e.getMessage());
            // L'exception sera gérée par GlobalExceptionHandler
            throw e;
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found for subscription: {}", e.getMessage());
            // L'exception sera gérée par GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during subscription for email {}: {}", 
                    request.getEmail(), e.getMessage(), e);
            // L'exception sera gérée par GlobalExceptionHandler
            throw new BadRequestException("Une erreur inattendue s'est produite lors de l'inscription. Veuillez réessayer.");
        }
    }
}

