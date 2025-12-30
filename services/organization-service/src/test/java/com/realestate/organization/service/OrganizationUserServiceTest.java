package com.realestate.organization.service;

import com.realestate.organization.entity.Organization;
import com.realestate.organization.entity.OrganizationUser;
import com.realestate.organization.entity.Team;
import com.realestate.organization.repository.OrganizationRepository;
import com.realestate.organization.repository.OrganizationUserRepository;
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
class OrganizationUserServiceTest {

    @Mock
    private OrganizationUserRepository organizationUserRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private OrganizationUserService organizationUserService;

    private Organization testOrganization;
    private Team testTeam;
    private OrganizationUser testOrganizationUser;

    @BeforeEach
    void setUp() {
        testOrganization = new Organization();
        testOrganization.setId(1L);
        testOrganization.setName("Test Organization");

        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setName("Test Team");
        testTeam.setOrganization(testOrganization);

        testOrganizationUser = new OrganizationUser();
        testOrganizationUser.setId(1L);
        testOrganizationUser.setUserId(100L);
        testOrganizationUser.setOrganization(testOrganization);
        testOrganizationUser.setActive(true);
    }

    @Test
    void testAddUserToOrganization_Success() {
        // Given
        Long userId = 100L;
        Long organizationId = 1L;
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(organizationUserRepository.findByUserIdAndOrganizationId(userId, organizationId))
                .thenReturn(Optional.empty());
        when(organizationUserRepository.save(any(OrganizationUser.class))).thenReturn(testOrganizationUser);

        // When
        OrganizationUser result = organizationUserService.addUserToOrganization(userId, organizationId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(organizationRepository).findById(organizationId);
        verify(organizationUserRepository).findByUserIdAndOrganizationId(userId, organizationId);
        verify(organizationUserRepository).save(any(OrganizationUser.class));
    }

    @Test
    void testAddUserToOrganization_UserAlreadyExists() {
        // Given
        Long userId = 100L;
        Long organizationId = 1L;
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(organizationUserRepository.findByUserIdAndOrganizationId(userId, organizationId))
                .thenReturn(Optional.of(testOrganizationUser));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                organizationUserService.addUserToOrganization(userId, organizationId));
        verify(organizationUserRepository, never()).save(any(OrganizationUser.class));
    }

    @Test
    void testAddUserToTeam_Success() {
        // Given
        Long userId = 100L;
        Long organizationId = 1L;
        Long teamId = 1L;
        when(organizationUserRepository.findByUserIdAndOrganizationId(userId, organizationId))
                .thenReturn(Optional.of(testOrganizationUser));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(testTeam));
        when(organizationUserRepository.save(any(OrganizationUser.class))).thenReturn(testOrganizationUser);

        // When
        OrganizationUser result = organizationUserService.addUserToTeam(userId, organizationId, teamId);

        // Then
        assertNotNull(result);
        assertEquals(testTeam, result.getTeam());
        verify(organizationUserRepository).findByUserIdAndOrganizationId(userId, organizationId);
        verify(teamRepository).findById(teamId);
        verify(organizationUserRepository).save(testOrganizationUser);
    }

    @Test
    void testAddUserToTeam_TeamNotInOrganization() {
        // Given
        Long userId = 100L;
        Long organizationId = 1L;
        Long teamId = 1L;
        Organization otherOrganization = new Organization();
        otherOrganization.setId(2L);
        testTeam.setOrganization(otherOrganization);

        when(organizationUserRepository.findByUserIdAndOrganizationId(userId, organizationId))
                .thenReturn(Optional.of(testOrganizationUser));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(testTeam));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                organizationUserService.addUserToTeam(userId, organizationId, teamId));
    }

    @Test
    void testGetUsersByOrganizationId_Success() {
        // Given
        Long organizationId = 1L;
        List<OrganizationUser> users = Arrays.asList(testOrganizationUser);
        when(organizationUserRepository.findActiveByOrganizationId(organizationId)).thenReturn(users);

        // When
        List<OrganizationUser> result = organizationUserService.getUsersByOrganizationId(organizationId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(organizationUserRepository).findActiveByOrganizationId(organizationId);
    }

    @Test
    void testUpdateUserRoles_Success() {
        // Given
        Long userId = 100L;
        Long organizationId = 1L;
        String customRoles = "[\"ADMIN\", \"MANAGER\"]";
        when(organizationUserRepository.findByUserIdAndOrganizationId(userId, organizationId))
                .thenReturn(Optional.of(testOrganizationUser));
        when(organizationUserRepository.save(any(OrganizationUser.class))).thenReturn(testOrganizationUser);

        // When
        OrganizationUser result = organizationUserService.updateUserRoles(userId, organizationId, customRoles);

        // Then
        assertNotNull(result);
        assertEquals(customRoles, result.getCustomRoles());
        verify(organizationUserRepository).findByUserIdAndOrganizationId(userId, organizationId);
        verify(organizationUserRepository).save(testOrganizationUser);
    }

    @Test
    void testRemoveUserFromOrganization_Success() {
        // Given
        Long userId = 100L;
        Long organizationId = 1L;
        when(organizationUserRepository.findByUserIdAndOrganizationId(userId, organizationId))
                .thenReturn(Optional.of(testOrganizationUser));
        doNothing().when(organizationUserRepository).delete(testOrganizationUser);

        // When
        assertDoesNotThrow(() -> organizationUserService.removeUserFromOrganization(userId, organizationId));

        // Then
        verify(organizationUserRepository).findByUserIdAndOrganizationId(userId, organizationId);
        verify(organizationUserRepository).delete(testOrganizationUser);
    }
}

