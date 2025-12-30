package com.realestate.organization.service;

import com.realestate.organization.entity.Organization;
import com.realestate.organization.entity.Team;
import com.realestate.organization.repository.OrganizationRepository;
import com.realestate.organization.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final OrganizationRepository organizationRepository;

    public TeamService(TeamRepository teamRepository, OrganizationRepository organizationRepository) {
        this.teamRepository = teamRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Team createTeam(Long organizationId, Team team) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + organizationId));
        team.setOrganization(organization);
        return teamRepository.save(team);
    }

    @Transactional(readOnly = true)
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Team> getTeamsByOrganizationId(Long organizationId) {
        return teamRepository.findActiveByOrganizationId(organizationId);
    }

    @Transactional
    public Team updateTeam(Long id, Team teamDetails) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));

        if (teamDetails.getName() != null) {
            team.setName(teamDetails.getName());
        }
        if (teamDetails.getDescription() != null) {
            team.setDescription(teamDetails.getDescription());
        }
        if (teamDetails.getActive() != null) {
            team.setActive(teamDetails.getActive());
        }

        return teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new RuntimeException("Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
    }
}

