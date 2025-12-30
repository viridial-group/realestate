package com.realestate.organization.service;

import com.realestate.organization.entity.Organization;
import com.realestate.organization.entity.OrganizationUser;
import com.realestate.organization.entity.Team;
import com.realestate.organization.repository.OrganizationRepository;
import com.realestate.organization.repository.OrganizationUserRepository;
import com.realestate.organization.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationUserService {

    private final OrganizationUserRepository organizationUserRepository;
    private final OrganizationRepository organizationRepository;
    private final TeamRepository teamRepository;

    public OrganizationUserService(
            OrganizationUserRepository organizationUserRepository,
            OrganizationRepository organizationRepository,
            TeamRepository teamRepository) {
        this.organizationUserRepository = organizationUserRepository;
        this.organizationRepository = organizationRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public OrganizationUser addUserToOrganization(Long userId, Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + organizationId));

        // Vérifier si l'utilisateur n'est pas déjà dans l'organisation
        Optional<OrganizationUser> existing = organizationUserRepository
                .findByUserIdAndOrganizationId(userId, organizationId);
        if (existing.isPresent()) {
            throw new RuntimeException("User already exists in organization");
        }

        OrganizationUser organizationUser = new OrganizationUser(userId, organization);
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

