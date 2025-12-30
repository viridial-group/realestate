package com.realestate.resource.service;

import com.realestate.resource.entity.Domain;
import com.realestate.resource.repository.DomainRepository;
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
class DomainServiceTest {

    @Mock
    private DomainRepository domainRepository;

    @InjectMocks
    private DomainService domainService;

    private Domain testDomain;

    @BeforeEach
    void setUp() {
        testDomain = new Domain();
        testDomain.setId(1L);
        testDomain.setName("Real Estate");
        testDomain.setDescription("Real estate properties domain");
        testDomain.setActive(true);
    }

    @Test
    void testCreateDomain_Success() {
        // Given
        when(domainRepository.save(any(Domain.class))).thenReturn(testDomain);

        // When
        Domain result = domainService.createDomain(testDomain);

        // Then
        assertNotNull(result);
        assertEquals(testDomain.getName(), result.getName());
        verify(domainRepository).save(testDomain);
    }

    @Test
    void testGetDomainById_Success() {
        // Given
        Long id = 1L;
        when(domainRepository.findById(id)).thenReturn(Optional.of(testDomain));

        // When
        Optional<Domain> result = domainService.getDomainById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDomain.getId(), result.get().getId());
        verify(domainRepository).findById(id);
    }

    @Test
    void testGetDomainById_NotFound() {
        // Given
        Long id = 999L;
        when(domainRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Domain> result = domainService.getDomainById(id);

        // Then
        assertFalse(result.isPresent());
        verify(domainRepository).findById(id);
    }

    @Test
    void testGetAllDomains_Success() {
        // Given
        List<Domain> domains = Arrays.asList(testDomain);
        when(domainRepository.findAll()).thenReturn(domains);

        // When
        List<Domain> result = domainService.getAllDomains();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(domainRepository).findAll();
    }

    @Test
    void testGetActiveDomains_Success() {
        // Given
        List<Domain> activeDomains = Arrays.asList(testDomain);
        when(domainRepository.findAllActive()).thenReturn(activeDomains);

        // When
        List<Domain> result = domainService.getActiveDomains();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(domainRepository).findAllActive();
    }

    @Test
    void testGetDomainByName_Success() {
        // Given
        String name = "Real Estate";
        when(domainRepository.findByName(name)).thenReturn(Optional.of(testDomain));

        // When
        Optional<Domain> result = domainService.getDomainByName(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(name, result.get().getName());
        verify(domainRepository).findByName(name);
    }

    @Test
    void testUpdateDomain_Success() {
        // Given
        Long id = 1L;
        Domain updateDetails = new Domain();
        updateDetails.setName("Updated Domain");
        updateDetails.setDescription("Updated Description");

        when(domainRepository.findById(id)).thenReturn(Optional.of(testDomain));
        when(domainRepository.save(any(Domain.class))).thenReturn(testDomain);

        // When
        Domain result = domainService.updateDomain(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Domain", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(domainRepository).findById(id);
        verify(domainRepository).save(testDomain);
    }

    @Test
    void testUpdateDomain_NotFound() {
        // Given
        Long id = 999L;
        Domain updateDetails = new Domain();
        when(domainRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> domainService.updateDomain(id, updateDetails));
        verify(domainRepository).findById(id);
        verify(domainRepository, never()).save(any(Domain.class));
    }

    @Test
    void testDeleteDomain_Success() {
        // Given
        Long id = 1L;
        when(domainRepository.existsById(id)).thenReturn(true);
        doNothing().when(domainRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> domainService.deleteDomain(id));

        // Then
        verify(domainRepository).existsById(id);
        verify(domainRepository).deleteById(id);
    }

    @Test
    void testDeleteDomain_NotFound() {
        // Given
        Long id = 999L;
        when(domainRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> domainService.deleteDomain(id));
        verify(domainRepository).existsById(id);
        verify(domainRepository, never()).deleteById(anyLong());
    }
}

