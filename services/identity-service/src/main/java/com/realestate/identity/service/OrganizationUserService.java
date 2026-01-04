package com.realestate.identity.service;

import com.realestate.identity.dto.OrganizationUserDTO;
import com.realestate.identity.entity.Organization;
import com.realestate.identity.entity.OrganizationUser;
import com.realestate.identity.entity.Team;
import com.realestate.identity.entity.User;
import com.realestate.identity.repository.OrganizationRepository;
import com.realestate.identity.repository.OrganizationUserRepository;
import com.realestate.identity.repository.TeamRepository;
import com.realestate.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizationUserService {

    private final OrganizationUserRepository organizationUserRepository;
    private final OrganizationRepository organizationRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;

    public OrganizationUserService(
            OrganizationUserRepository organizationUserRepository,
            OrganizationRepository organizationRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            RoleService roleService) {
        this.organizationUserRepository = organizationUserRepository;
        this.organizationRepository = organizationRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Transactional
    public OrganizationUser addUserToOrganization(Long userId, Long organizationId, Boolean isPrimary) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + organizationId));

        // Vérifier si l'utilisateur n'est pas déjà dans l'organisation
        Optional<OrganizationUser> existing = organizationUserRepository
                .findByUserIdAndOrganizationId(userId, organizationId);
        if (existing.isPresent()) {
            throw new RuntimeException("User already exists in organization");
        }

        OrganizationUser organizationUser = new OrganizationUser(userId, organization);
        
        // Si c'est l'affectation principale, désactiver les autres affectations principales
        if (isPrimary != null && isPrimary) {
            organizationUserRepository.findAllByUserId(userId).forEach(ou -> {
                if (ou.getIsPrimary()) {
                    ou.setIsPrimary(false);
                    organizationUserRepository.save(ou);
                }
            });
            organizationUser.setIsPrimary(true);
            
            // Assigner automatiquement le rôle ORGANIZATION_ADMIN au créateur de l'organisation
            // Ce rôle donne les permissions pour gérer l'organisation et toutes ses sous-organisations
            try {
                roleService.assignRoleToUserByName(userId, "ORGANIZATION_ADMIN");
            } catch (Exception e) {
                // Log l'erreur mais ne bloque pas l'ajout de l'utilisateur à l'organisation
                // Le rôle pourra être assigné manuellement plus tard
                System.err.println("Warning: Could not assign ORGANIZATION_ADMIN role to user " + userId + ": " + e.getMessage());
            }
        } else {
            organizationUser.setIsPrimary(false);
        }
        
        return organizationUserRepository.save(organizationUser);
    }

    @Transactional
    public OrganizationUser addUserToOrganization(Long userId, Long organizationId) {
        return addUserToOrganization(userId, organizationId, false);
    }

    @Transactional
    public OrganizationUser setPrimaryOrganization(Long userId, Long organizationId) {
        OrganizationUser organizationUser = organizationUserRepository
                .findByUserIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new RuntimeException("User not found in organization"));

        // Désactiver toutes les autres affectations principales
        organizationUserRepository.findAllByUserId(userId).forEach(ou -> {
            if (!ou.getId().equals(organizationUser.getId()) && ou.getIsPrimary()) {
                ou.setIsPrimary(false);
                organizationUserRepository.save(ou);
            }
        });

        organizationUser.setIsPrimary(true);
        return organizationUserRepository.save(organizationUser);
    }

    @Transactional
    public OrganizationUser addUserToTeam(Long userId, Long organizationId, Long teamId) {
        OrganizationUser organizationUser = organizationUserRepository
                .findByUserIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new RuntimeException("User not found in organization"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));

        if (!team.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Team does not belong to the organization");
        }

        organizationUser.setTeam(team);
        return organizationUserRepository.save(organizationUser);
    }

    @Transactional(readOnly = true)
    public List<OrganizationUser> getUsersByOrganizationId(Long organizationId) {
        return organizationUserRepository.findActiveByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public List<OrganizationUserDTO> getUsersByOrganizationIdWithUserInfo(Long organizationId) {
        List<OrganizationUser> organizationUsers = organizationUserRepository.findActiveByOrganizationId(organizationId);
        
        return organizationUsers.stream().map(orgUser -> {
            OrganizationUserDTO dto = new OrganizationUserDTO();
            dto.setId(orgUser.getId());
            dto.setUserId(orgUser.getUserId());
            dto.setActive(orgUser.getActive());
            dto.setIsPrimary(orgUser.getIsPrimary());
            dto.setCustomRoles(orgUser.getCustomRoles());
            dto.setCreatedAt(orgUser.getCreatedAt());
            dto.setUpdatedAt(orgUser.getUpdatedAt());
            
            // Organization info
            if (orgUser.getOrganization() != null) {
                dto.setOrganizationId(orgUser.getOrganization().getId());
                dto.setOrganizationName(orgUser.getOrganization().getName());
            }
            
            // Team info
            if (orgUser.getTeam() != null) {
                dto.setTeamId(orgUser.getTeam().getId());
            }
            
            // User info
            Optional<User> userOpt = userRepository.findById(orgUser.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                dto.setUserEmail(user.getEmail());
                dto.setUserFirstName(user.getFirstName());
                dto.setUserLastName(user.getLastName());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrganizationUser> getUsersByTeamId(Long teamId) {
        return organizationUserRepository.findByTeamId(teamId);
    }

    @Transactional
    public void removeUserFromOrganization(Long userId, Long organizationId) {
        OrganizationUser organizationUser = organizationUserRepository
                .findByUserIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new RuntimeException("User not found in organization"));
        organizationUserRepository.delete(organizationUser);
    }

    @Transactional
    public OrganizationUser updateUserRoles(Long userId, Long organizationId, String customRoles) {
        OrganizationUser organizationUser = organizationUserRepository
                .findByUserIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new RuntimeException("User not found in organization"));
        organizationUser.setCustomRoles(customRoles);
        return organizationUserRepository.save(organizationUser);
    }
}

