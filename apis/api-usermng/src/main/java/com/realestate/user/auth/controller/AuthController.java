package com.realestate.user.auth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.common.exception.DuplicateResourceException;
import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.user.auth.dto.LoginRequest;
import com.realestate.user.auth.dto.LoginResponse;
import com.realestate.user.auth.dto.SignupRequest;
import com.realestate.common.security.JwtService;
import com.realestate.user.service.AuthorizationService;
import com.realestate.user.organization.service.OrganizationService;
import com.realestate.user.organization.entity.Organization;
import com.realestate.user.organization.repository.OrganizationRepository;
import com.realestate.user.entity.User;
import com.realestate.user.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication", description = "API d'authentification et gestion des utilisateurs")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationService authorizationService;
    private final OrganizationService organizationService;

    @Operation(
        summary = "Connexion utilisateur",
        description = "Authentifie un utilisateur et retourne un token JWT pour accéder aux autres endpoints de l'API."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // 1️⃣ Authentification username/password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getEmail()));

        // Génération du JWT
        Set<String> permissions = authorizationService.computePermissions(user.getRoles());
        List<UUID> tenants = organizationService.getSubTreeIds(user.getOrganization().getId());
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .toList();
        
        String token = jwtService.generateToken(
                user.getId(),
                user.getOrganization().getId(),
                tenants,
                roles,
                permissions,
                user.getPreferredLanguage()
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/hash")
    public ResponseEntity<Map<String, String>> hashPassword(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        if (password == null || password.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Le mot de passe est requis");
            return ResponseEntity.badRequest().body(error);
        }
        
        String hash = passwordEncoder.encode(password);
        Map<String, String> response = new HashMap<>();
        response.put("password", password);
        response.put("hash", hash);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // Vérifier que l'email n'existe pas déjà
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", request.getEmail(), "Cet email est déjà utilisé");
        }

        // Vérifier que l'organisation existe
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", request.getOrganizationId()));

        // Créer le nouvel utilisateur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOrganization(organization);

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        // Générer le token JWT
        Set<String> permissions = authorizationService.computePermissions(savedUser.getRoles());
        List<UUID> tenants = organizationService.getSubTreeIds(savedUser.getOrganization().getId());
        List<String> roles = savedUser.getRoles().stream()
                .map(role -> role.getName())
                .toList();
        
        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getOrganization().getId(),
                tenants,
                roles,
                permissions,
                savedUser.getPreferredLanguage()
        );

        // Retourner la réponse avec le token
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Utilisateur créé avec succès");
        response.put("token", token);
        response.put("user", Map.of(
            "id", savedUser.getId().toString(),
            "email", savedUser.getEmail()
        ));

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }
}

