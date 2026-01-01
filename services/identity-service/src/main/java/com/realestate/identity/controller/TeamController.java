package com.realestate.identity.controller;

import com.realestate.identity.dto.TeamDTO;
import com.realestate.identity.entity.Team;
import com.realestate.identity.mapper.TeamMapper;
import com.realestate.identity.service.TeamService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/identity/organizations/{organizationId}/teams")
@Tag(name = "Teams", description = "Team management API")
@SecurityRequirement(name = "bearerAuth")
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    @PostMapping
    @Operation(summary = "Create team", description = "Creates a new team for an organization")
    public ResponseEntity<TeamDTO> createTeam(
            @PathVariable Long organizationId,
            @Valid @RequestBody TeamDTO teamDTO) {
        Team team = teamMapper.toEntity(teamDTO);
        Team created = teamService.createTeam(organizationId, team);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID", description = "Returns team information for a specific ID")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        Team team = teamService.getTeamById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        return ResponseEntity.ok(teamMapper.toDTO(team));
    }

    @GetMapping
    @Operation(summary = "Get teams by organization", description = "Returns all teams for an organization")
    public ResponseEntity<List<TeamDTO>> getTeamsByOrganization(@PathVariable Long organizationId) {
        List<Team> teams = teamService.getTeamsByOrganizationId(organizationId);
        List<TeamDTO> teamDTOs = teams.stream()
                .map(teamMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teamDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update team", description = "Updates an existing team")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamDTO teamDTO) {
        Team team = teamMapper.toEntity(teamDTO);
        Team updated = teamService.updateTeam(id, team);
        return ResponseEntity.ok(teamMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team", description = "Deletes a team by ID")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

