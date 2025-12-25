package com.realestate.user.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.user.organization.service.OrganizationService;
import com.realestate.user.entity.User;
import com.realestate.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationService organizationService;
    private final AuthorizationService authorizationService;
    
    /**
     * Récupère les utilisateurs accessibles (optimisé avec requête WHERE IN)
     * Utilise read replica si configuré (@Transactional(readOnly=true))
     */
    @Transactional(readOnly = true)
    public List<User> getUsersAccessible(UUID userOrgId) {
        List<UUID> allowedOrgIds = organizationService.getSubTreeIds(userOrgId);
        
        if (allowedOrgIds.isEmpty()) {
            return List.of();
        }
        
        return userRepository.findByOrganizationIdIn(allowedOrgIds);
    }

    public Set<String> getUserEffectivePermissions(User user) {
        return authorizationService.computePermissions(user.getRoles());
    }

    /**
     * Récupère un utilisateur par son ID
     * Utilise read replica si configuré (@Transactional(readOnly=true))
     */
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    /**
     * Met à jour la langue préférée de l'utilisateur
     */
    public User updatePreferredLanguage(UUID userId, String language) {
        User user = getUserById(userId);
        user.setPreferredLanguage(language);
        return userRepository.save(user);
    }

    /**
     * Récupère tous les utilisateurs d'une organisation
     * Utilise read replica si configuré (@Transactional(readOnly=true))
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByOrganizationId(UUID organizationId) {
        return userRepository.findByOrganizationId(organizationId);
    }
}

