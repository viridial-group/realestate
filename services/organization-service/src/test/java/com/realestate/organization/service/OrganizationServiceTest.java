package com.realestate.organization.service;

import com.realestate.organization.entity.Organization;
import com.realestate.organization.repository.OrganizationRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationService organizationService;

    private Organization testOrganization;
    private Organization parentOrganization;

    @BeforeEach
    void setUp() {
        testOrganization = new Organization();
        testOrganization.setId(1L);
        testOrganization.setName("Test Organization");
        testOrganization.setDescription("Test Description");
        testOrganization.setDomain("test.com");
        testOrganization.setActive(true);

        parentOrganization = new Organization();
        parentOrganization.setId(2L);
        parentOrganization.setName("Parent Organization");
    }

    @Test
    void testCreateOrganization_Success() {
        // Given
        when(organizationRepository.save(any(Organization.class))).thenReturn(testOrganization);

        // When
        Organization result = organizationService.createOrganization(testOrganization);

        // Then
        assertNotNull(result);
        assertEquals(testOrganization.getName(), result.getName());
        verify(organizationRepository).save(testOrganization);
    }

    @Test
    void testGetOrganizationById_Success() {
        // Given
        Long id = 1L;
        when(organizationRepository.findById(id)).thenReturn(Optional.of(testOrganization));

        // When
        Optional<Organization> result = organizationService.getOrganizationById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testOrganization.getId(), result.get().getId());
        verify(organizationRepository).findById(id);
    }

    @Test
    void testGetOrganizationById_NotFound() {
        // Given
        Long id = 999L;
        when(organizationRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Organization> result = organizationService.getOrganizationById(id);

        // Then
        assertFalse(result.isPresent());
        verify(organizationRepository).findById(id);
    }

    @Test
    void testGetAllOrganizations_Success() {
        // Given
        List<Organization> organizations = Arrays.asList(testOrganization, parentOrganization);
        when(organizationRepository.findAll()).thenReturn(organizations);

        // When
        List<Organization> result = organizationService.getAllOrganizations();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(organizationRepository).findAll();
    }

    @Test
    void testGetOrganizationsByUserId_Success() {
        // Given
        Long userId = 1L;
        List<Organization> organizations = Arrays.asList(testOrganization);
        when(organizationRepository.findByUserId(userId)).thenReturn(organizations);

        // When
        List<Organization> result = organizationService.getOrganizationsByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(organizationRepository).findByUserId(userId);
    }

    @Test
    void testGetRootOrganizations_Success() {
        // Given
        List<Organization> rootOrganizations = Arrays.asList(parentOrganization);
        when(organizationRepository.findByParentIsNull()).thenReturn(rootOrganizations);

        // When
        List<Organization> result = organizationService.getRootOrganizations();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(organizationRepository).findByParentIsNull();
    }

    @Test
    void testGetChildren_Success() {
        // Given
        Long parentId = 2L;
        List<Organization> children = Arrays.asList(testOrganization);
        when(organizationRepository.findByParentId(parentId)).thenReturn(children);

        // When
        List<Organization> result = organizationService.getChildren(parentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(organizationRepository).findByParentId(parentId);
    }

    @Test
    void testUpdateOrganization_Success() {
        // Given
        Long id = 1L;
        Organization updateDetails = new Organization();
        updateDetails.setName("Updated Name");
        updateDetails.setDescription("Updated Description");

        when(organizationRepository.findById(id)).thenReturn(Optional.of(testOrganization));
        when(organizationRepository.save(any(Organization.class))).thenReturn(testOrganization);

        // When
        Organization result = organizationService.updateOrganization(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(organizationRepository).findById(id);
        verify(organizationRepository).save(testOrganization);
    }

    @Test
    void testUpdateOrganization_NotFound() {
        // Given
        Long id = 999L;
        Organization updateDetails = new Organization();
        when(organizationRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> organizationService.updateOrganization(id, updateDetails));
        verify(organizationRepository).findById(id);
        verify(organizationRepository, never()).save(any(Organization.class));
    }

    @Test
    void testDeleteOrganization_Success() {
        // Given
        Long id = 1L;
        when(organizationRepository.existsById(id)).thenReturn(true);
        doNothing().when(organizationRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> organizationService.deleteOrganization(id));

        // Then
        verify(organizationRepository).existsById(id);
        verify(organizationRepository).deleteById(id);
    }

    @Test
    void testDeleteOrganization_NotFound() {
        // Given
        Long id = 999L;
        when(organizationRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> organizationService.deleteOrganization(id));
        verify(organizationRepository).existsById(id);
        verify(organizationRepository, never()).deleteById(anyLong());
    }
}

