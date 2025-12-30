package com.realestate.organization.mapper;

import com.realestate.organization.dto.TeamDTO;
import com.realestate.organization.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public TeamDTO toDTO(Team team) {
        if (team == null) {
            return null;
        }
        TeamDTO dto = new TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setActive(team.getActive());
        
        // Mapper seulement l'ID de l'organisation (évite lazy loading)
        if (team.getOrganization() != null) {
            dto.setOrganizationId(team.getOrganization().getId());
        }
        
        dto.setCreatedAt(team.getCreatedAt());
        dto.setUpdatedAt(team.getUpdatedAt());
        return dto;
    }

    public Team toEntity(TeamDTO dto) {
        if (dto == null) {
            return null;
        }
        Team team = new Team();
        team.setId(dto.getId());
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());
        team.setActive(dto.getActive());
        // Note: organization doit être gérée séparément
        return team;
    }
}

