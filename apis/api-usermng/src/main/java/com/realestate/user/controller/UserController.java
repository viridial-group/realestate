package com.realestate.user.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.common.i18n.I18nService;
import com.realestate.common.service.CurrentUserService;
import com.realestate.user.entity.User;
import com.realestate.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Users", description = "API de gestion des utilisateurs")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;
    private final I18nService i18nService;

    @Operation(summary = "Récupère les utilisateurs accessibles", 
               description = "Récupère tous les utilisateurs accessibles par l'organisation de l'utilisateur connecté")
    @GetMapping("/accessible/{orgId}")
    public List<User> getAccessibleUsers(@PathVariable UUID orgId) {
        return userService.getUsersAccessible(orgId);
    }

    @Operation(summary = "Récupère les permissions d'un utilisateur",
               description = "Récupère toutes les permissions effectives d'un utilisateur (inclut les permissions héritées des rôles)")
    @GetMapping("/{userId}/permissions")
    public Set<String> getUserPermissions(@PathVariable UUID userId) {
        User user = userService.getUserById(userId);
        return userService.getUserEffectivePermissions(user);
    }

    @Operation(summary = "Met à jour la langue préférée de l'utilisateur connecté",
               description = "Change la langue préférée de l'utilisateur connecté. Langues supportées: fr, en, es, de, it")
    @PatchMapping("/me/preferred-language")
    public ResponseEntity<Map<String, Object>> updatePreferredLanguage(
            @RequestBody Map<String, String> request) {
        String language = request.get("language");
        if (language == null || language.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", i18nService.getMessage("error.validation.field.required", "language")
            ));
        }

        UUID userId = currentUserService.getCurrentUserId();
        User updatedUser = userService.updatePreferredLanguage(userId, language);
        
        return ResponseEntity.ok(Map.of(
            "message", i18nService.getMessage("success.updated"),
            "userId", updatedUser.getId().toString(),
            "preferredLanguage", updatedUser.getPreferredLanguage()
        ));
    }

    @Operation(summary = "Récupère les informations de l'utilisateur connecté",
               description = "Récupère les informations complètes de l'utilisateur actuellement connecté")
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        UUID userId = currentUserService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Récupère un utilisateur par ID",
               description = "Récupère les informations d'un utilisateur par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Récupère l'email d'un utilisateur",
               description = "Récupère l'email d'un utilisateur par son ID")
    @GetMapping("/{id}/email")
    public ResponseEntity<String> getUserEmail(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user.getEmail());
    }

    @Operation(summary = "Récupère les utilisateurs d'une organisation",
               description = "Récupère tous les utilisateurs d'une organisation spécifique")
    @GetMapping("/by-organization/{organizationId}")
    public ResponseEntity<List<User>> getUsersByOrganizationId(@PathVariable UUID organizationId) {
        List<User> users = userService.getUsersByOrganizationId(organizationId);
        return ResponseEntity.ok(users);
    }
}
