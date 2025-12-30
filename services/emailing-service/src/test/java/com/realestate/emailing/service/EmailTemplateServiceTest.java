package com.realestate.emailing.service;

import com.realestate.emailing.entity.EmailTemplate;
import com.realestate.emailing.repository.EmailTemplateRepository;
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
class EmailTemplateServiceTest {

    @Mock
    private EmailTemplateRepository templateRepository;

    @InjectMocks
    private EmailTemplateService templateService;

    private EmailTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testTemplate = new EmailTemplate();
        testTemplate.setId(1L);
        testTemplate.setName("welcome_email");
        testTemplate.setType("WELCOME");
        testTemplate.setSubject("Welcome {{name}}");
        testTemplate.setBody("Hello {{name}}!");
        testTemplate.setOrganizationId(100L);
        testTemplate.setActive(true);
        testTemplate.setIsDefault(true);
    }

    @Test
    void testCreateTemplate_Success() {
        // Given
        when(templateRepository.save(any(EmailTemplate.class))).thenReturn(testTemplate);

        // When
        EmailTemplate result = templateService.createTemplate(testTemplate);

        // Then
        assertNotNull(result);
        assertEquals(testTemplate.getName(), result.getName());
        verify(templateRepository).save(testTemplate);
    }

    @Test
    void testGetTemplateById_Success() {
        // Given
        Long id = 1L;
        when(templateRepository.findById(id)).thenReturn(Optional.of(testTemplate));

        // When
        Optional<EmailTemplate> result = templateService.getTemplateById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTemplate.getId(), result.get().getId());
        verify(templateRepository).findById(id);
    }

    @Test
    void testGetTemplatesByTypeAndOrganization_Success() {
        // Given
        String type = "WELCOME";
        Long organizationId = 100L;
        List<EmailTemplate> templates = Arrays.asList(testTemplate);
        when(templateRepository.findActiveByTypeAndOrganization(type, organizationId)).thenReturn(templates);

        // When
        List<EmailTemplate> result = templateService.getTemplatesByTypeAndOrganization(type, organizationId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(templateRepository).findActiveByTypeAndOrganization(type, organizationId);
    }

    @Test
    void testUpdateTemplate_Success() {
        // Given
        Long id = 1L;
        EmailTemplate updateDetails = new EmailTemplate();
        updateDetails.setName("Updated Name");
        updateDetails.setSubject("Updated Subject");

        when(templateRepository.findById(id)).thenReturn(Optional.of(testTemplate));
        when(templateRepository.save(any(EmailTemplate.class))).thenReturn(testTemplate);

        // When
        EmailTemplate result = templateService.updateTemplate(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Subject", result.getSubject());
        verify(templateRepository).findById(id);
        verify(templateRepository).save(testTemplate);
    }

    @Test
    void testDeleteTemplate_Success() {
        // Given
        Long id = 1L;
        when(templateRepository.existsById(id)).thenReturn(true);
        doNothing().when(templateRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> templateService.deleteTemplate(id));

        // Then
        verify(templateRepository).existsById(id);
        verify(templateRepository).deleteById(id);
    }
}

