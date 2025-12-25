package com.realestate.user.organization.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.user.organization.entity.Organization;
import com.realestate.user.organization.repository.OrganizationRepository;
import com.realestate.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository repository;
    private final UserRepository userRepository;

    /**
     * Récupère les IDs des organisations accessibles (mis en cache, optimisé avec requête SQL récursive)
     * La requête récursive inclut l'organisation racine et tous ses descendants
     * Utilise read replica si configuré (@Transactional(readOnly=true))
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "organizations", key = "'subtree-ids:' + #rootId")
    public List<UUID> getSubTreeIds(UUID rootId) {
        log.debug("Fetching subtree IDs for organization: rootId={}", rootId);
        List<UUID> result = repository.findSubTreeIds(rootId);
        // S'assurer que la racine est toujours incluse
        if (!result.contains(rootId)) {
            log.debug("Root organization not in result, adding it: rootId={}", rootId);
            result.add(0, rootId);
        }
        log.debug("Subtree IDs retrieved: rootId={}, count={}", rootId, result.size());
        return result;
    }

    /**
     * Invalide le cache pour un rootId spécifique
     */
    @CacheEvict(value = "organizations", key = "'subtree-ids:' + #rootId")
    public void evictSubTreeCache(UUID rootId) {
        // Méthode pour invalider le cache
    }

    /**
     * Récupère l'arbre des organisations (optimisé - charge uniquement les IDs puis récupère les entités)
     * Utilise une requête optimisée pour éviter de charger récursivement toutes les relations
     * Utilise read replica si configuré (@Transactional(readOnly=true))
     */
    @Transactional(readOnly = true)
    public List<Organization> getSubTree(UUID rootId) {
        List<UUID> orgIds = getSubTreeIds(rootId);
        
        if (orgIds.isEmpty()) {
            return List.of();
        }
        
        // Charger toutes les organisations en une seule requête
        return repository.findByIdIn(orgIds);
    }

    /**
     * Récupère une organisation par son ID
     * Utilise read replica si configuré (@Transactional(readOnly=true))
     */
    @Transactional(readOnly = true)
    public Organization getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
    }

    /**
     * Récupère tous les utilisateurs d'une organisation
     * Utilise read replica si configuré (@Transactional(readOnly=true))
     */
    @Transactional(readOnly = true)
    public List<com.realestate.user.entity.User> getUsersByOrganizationId(UUID organizationId) {
        return userRepository.findByOrganizationId(organizationId);
    }
}

