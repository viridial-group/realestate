package com.realestate.identity.service;

import com.realestate.identity.entity.OrganizationUser;
import com.realestate.identity.entity.Role;
import com.realestate.identity.entity.User;
import com.realestate.identity.repository.OrganizationUserRepository;
import com.realestate.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service pour déterminer et gérer le type d'utilisateur (Individuel vs Professionnel)
 */
@Service
public class UserTypeService {

    private final UserRepository userRepository;
    private final OrganizationUserRepository organizationUserRepository;

    public UserTypeService(
            UserRepository userRepository,
            OrganizationUserRepository organizationUserRepository) {
        this.userRepository = userRepository;
        this.organizationUserRepository = organizationUserRepository;
    }

    /**
     * Enum pour les types d'utilisateurs
     */
    public enum UserType {
        INDIVIDUAL,      // Particulier
        PROFESSIONAL,    // Professionnel (avec organisation)
        UNKNOWN          // Type non déterminé
    }

    /**
     * Détermine le type d'utilisateur basé sur ses rôles et organisations
     * 
     * @param user L'utilisateur à analyser
     * @return Le type d'utilisateur
     */
    @Transactional(readOnly = true)
    public UserType determineUserType(User user) {
        if (user == null || user.getId() == null) {
            return UserType.UNKNOWN;
        }

        // Charger les rôles si nécessaire
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user = userRepository.findByIdWithRoles(user.getId())
                    .orElse(user);
        }

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Vérifier si l'utilisateur a une organisation
        boolean hasOrganization = !organizationUserRepository.findByUserId(user.getId()).isEmpty();

        // Logique de détermination
        if (hasOrganization || roleNames.contains("ORGANIZATION_ADMIN") || 
            roleNames.contains("MANAGER") || roleNames.contains("USER")) {
            // Si l'utilisateur a une organisation ou des rôles professionnels
            return UserType.PROFESSIONAL;
        } else if (roleNames.contains("INDIVIDUAL")) {
            // Si l'utilisateur a le rôle INDIVIDUAL et pas d'organisation
            return UserType.INDIVIDUAL;
        }

        // Par défaut, si l'utilisateur a une organisation, c'est un professionnel
        return hasOrganization ? UserType.PROFESSIONAL : UserType.INDIVIDUAL;
    }

    /**
     * Détermine le type d'utilisateur par ID
     */
    @Transactional(readOnly = true)
    public UserType determineUserType(Long userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElse(null);
        
        if (user == null) {
            return UserType.UNKNOWN;
        }

        return determineUserType(user);
    }

    /**
     * Vérifie si l'utilisateur est un particulier
     */
    @Transactional(readOnly = true)
    public boolean isIndividual(User user) {
        return determineUserType(user) == UserType.INDIVIDUAL;
    }

    /**
     * Vérifie si l'utilisateur est un professionnel
     */
    @Transactional(readOnly = true)
    public boolean isProfessional(User user) {
        return determineUserType(user) == UserType.PROFESSIONAL;
    }

    /**
     * Vérifie si l'utilisateur est un particulier par ID
     */
    @Transactional(readOnly = true)
    public boolean isIndividual(Long userId) {
        return determineUserType(userId) == UserType.INDIVIDUAL;
    }

    /**
     * Vérifie si l'utilisateur est un professionnel par ID
     */
    @Transactional(readOnly = true)
    public boolean isProfessional(Long userId) {
        return determineUserType(userId) == UserType.PROFESSIONAL;
    }

    /**
     * Récupère l'organisation principale de l'utilisateur (pour les professionnels)
     * 
     * @param userId ID de l'utilisateur
     * @return L'ID de l'organisation principale, ou null si l'utilisateur n'a pas d'organisation
     */
    @Transactional(readOnly = true)
    public Long getPrimaryOrganizationId(Long userId) {
        return organizationUserRepository.findPrimaryByUserId(userId)
                .map(OrganizationUser::getOrganization)
                .map(org -> org.getId())
                .orElse(null);
    }

    /**
     * Récupère toutes les organisations de l'utilisateur
     */
    @Transactional(readOnly = true)
    public Set<Long> getUserOrganizationIds(Long userId) {
        return organizationUserRepository.findByUserId(userId).stream()
                .map(OrganizationUser::getOrganization)
                .map(org -> org.getId())
                .collect(Collectors.toSet());
    }

    /**
     * Vérifie si l'utilisateur peut gérer une propriété
     * - Individuel : seulement ses propres propriétés (createdBy = userId)
     * - Professionnel : propriétés de son organisation (organizationId = userOrganizationId)
     */
    @Transactional(readOnly = true)
    public boolean canManageProperty(Long userId, Long propertyCreatedBy, Long propertyOrganizationId) {
        UserType userType = determineUserType(userId);

        if (userType == UserType.INDIVIDUAL) {
            // Les particuliers ne peuvent gérer que leurs propres propriétés
            return propertyCreatedBy != null && propertyCreatedBy.equals(userId);
        } else if (userType == UserType.PROFESSIONAL) {
            // Les professionnels peuvent gérer les propriétés de leur organisation
            Set<Long> userOrgIds = getUserOrganizationIds(userId);
            return propertyOrganizationId != null && userOrgIds.contains(propertyOrganizationId);
        }

        return false;
    }
}

