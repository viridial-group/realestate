package com.realestate.identity.controller;

import com.realestate.identity.dto.PasswordHashRequest;
import com.realestate.identity.dto.PasswordHashResponse;
import com.realestate.identity.service.PasswordHashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for password hash generation
 * This endpoint is public and does not require authentication
 */
@RestController
@RequestMapping("/api/identity/utils")
@Tag(name = "Utilities", description = "Utility endpoints for password hash generation")
public class PasswordHashController {

    private final PasswordHashService passwordHashService;

    public PasswordHashController(PasswordHashService passwordHashService) {
        this.passwordHashService = passwordHashService;
    }

    @PostMapping("/password-hash")
    @Operation(
            summary = "Generate password hash",
            description = "Generates a BCrypt hash for the given password. This endpoint is public and does not require authentication."
    )
    public ResponseEntity<PasswordHashResponse> generatePasswordHash(@Valid @RequestBody PasswordHashRequest request) {
        String hash = passwordHashService.generateHash(request.getPassword());
        PasswordHashResponse response = new PasswordHashResponse(
                request.getPassword(),
                hash,
                "BCrypt"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/password-hash")
    @Operation(
            summary = "Generate password hash (GET)",
            description = "Generates a BCrypt hash for the given password using query parameter. This endpoint is public and does not require authentication."
    )
    public ResponseEntity<PasswordHashResponse> generatePasswordHashGet(
            @RequestParam @Valid @jakarta.validation.constraints.NotBlank String password) {
        String hash = passwordHashService.generateHash(password);
        PasswordHashResponse response = new PasswordHashResponse(
                password,
                hash,
                "BCrypt"
        );
        return ResponseEntity.ok(response);
    }
}

