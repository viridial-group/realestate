package com.realestate.organization.controller;

import com.realestate.organization.dto.TeamDTO;
import com.realestate.organization.entity.Team;
import com.realestate.organization.mapper.TeamMapper;
import com.realestate.organization.service.TeamService;
import com.realestate.common.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizations/{organizationId}/teams")
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(
            @PathVariable Long organizationId,
            @Valid @RequestBody TeamDTO teamDTO) {
        Team team = teamMapper.toEntity(teamDTO);
        Team created = teamService.createTeam(organizationId, team);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        Team team = teamService.getTeamById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        return ResponseEntity.ok(teamMapper.toDTO(team));
    }

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getTeamsByOrganization(@PathVariable Long organizationId) {
        List<Team> teams = teamService.getTeamsByOrganizationId(organizationId);
        List<TeamDTO> teamDTOs = teams.stream()
                .map(teamMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teamDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamDTO teamDTO) {
        Team team = teamMapper.toEntity(teamDTO);
        Team updated = teamService.updateTeam(id, team);
        return ResponseEntity.ok(teamMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

