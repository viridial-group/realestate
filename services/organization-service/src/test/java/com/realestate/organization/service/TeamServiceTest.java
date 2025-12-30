package com.realestate.organization.service;

import com.realestate.organization.entity.Organization;
import com.realestate.organization.entity.Team;
import com.realestate.organization.repository.OrganizationRepository;
import com.realestate.organization.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private TeamService teamService;

    private Organization testOrganization;
    private Team testTeam;

    @BeforeEach
    void setUp() {
        testOrganization = new Organization();
        testOrganization.setId(1L);
        testOrganization.setName("Test Organization");

        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setName("Test Team");
        testTeam.setDescription("Test Team Description");
        testTeam.setOrganization(testOrganization);
        testTeam.setActive(true);
    }

    @Test
    void testCreateTeam_Success() {
        // Given
        Long organizationId = 1L;
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        Team result = teamService.createTeam(organizationId, testTeam);

        // Then
        assertNotNull(result);
        assertEquals(testTeam.getName(), result.getName());
        assertEquals(testOrganization, result.getOrganization());
        verify(organizationRepository).findById(organizationId);
        verify(teamRepository).save(testTeam);
    }

    @Test
    void testCreateTeam_OrganizationNotFound() {
        // Given
        Long organizationId = 999L;
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> teamService.createTeam(organizationId, testTeam));
        verify(organizationRepository).findById(organizationId);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testGetTeamById_Success() {
        // Given
        Long id = 1L;
        when(teamRepository.findById(id)).thenReturn(Optional.of(testTeam));

        // When
        Optional<Team> result = teamService.getTeamById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTeam.getId(), result.get().getId());
        verify(teamRepository).findById(id);
    }

    @Test
    void testGetTeamsByOrganizationId_Success() {
        // Given
        Long organizationId = 1L;
        List<Team> teams = Arrays.asList(testTeam);
        when(teamRepository.findActiveByOrganizationId(organizationId)).thenReturn(teams);

        // When
        List<Team> result = teamService.getTeamsByOrganizationId(organizationId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(teamRepository).findActiveByOrganizationId(organizationId);
    }

    @Test
    void testUpdateTeam_Success() {
        // Given
        Long id = 1L;
        Team updateDetails = new Team();
        updateDetails.setName("Updated Team Name");
        updateDetails.setDescription("Updated Description");

        when(teamRepository.findById(id)).thenReturn(Optional.of(testTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        // When
        Team result = teamService.updateTeam(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Team Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(teamRepository).findById(id);
        verify(teamRepository).save(testTeam);
    }

    @Test
    void testUpdateTeam_NotFound() {
        // Given
        Long id = 999L;
        Team updateDetails = new Team();
        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> teamService.updateTeam(id, updateDetails));
        verify(teamRepository).findById(id);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testDeleteTeam_Success() {
        // Given
        Long id = 1L;
        when(teamRepository.existsById(id)).thenReturn(true);
        doNothing().when(teamRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> teamService.deleteTeam(id));

        // Then
        verify(teamRepository).existsById(id);
        verify(teamRepository).deleteById(id);
    }

    @Test
    void testDeleteTeam_NotFound() {
        // Given
        Long id = 999L;
        when(teamRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> teamService.deleteTeam(id));
        verify(teamRepository).existsById(id);
        verify(teamRepository, never()).deleteById(anyLong());
    }
}

