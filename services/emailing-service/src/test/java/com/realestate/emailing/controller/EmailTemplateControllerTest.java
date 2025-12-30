package com.realestate.emailing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.emailing.entity.EmailTemplate;
import com.realestate.emailing.service.EmailTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmailTemplateController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class EmailTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailTemplateService templateService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmailTemplate createTestTemplate() {
        EmailTemplate template = new EmailTemplate();
        template.setId(1L);
        template.setName("welcome_email");
        template.setType("WELCOME");
        template.setSubject("Welcome {{name}}");
        template.setBody("Hello {{name}}!");
        template.setOrganizationId(100L);
        template.setActive(true);
        template.setIsDefault(true);
        return template;
    }

    @Test
    void testCreateTemplate_Success() throws Exception {
        // Given
        EmailTemplate template = createTestTemplate();
        when(templateService.createTemplate(any(EmailTemplate.class))).thenReturn(template);

        // When & Then
        mockMvc.perform(post("/api/emails/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(template)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("welcome_email"));
    }

    @Test
    void testGetTemplateById_Success() throws Exception {
        // Given
        Long id = 1L;
        EmailTemplate template = createTestTemplate();
        when(templateService.getTemplateById(id)).thenReturn(Optional.of(template));

        // When & Then
        mockMvc.perform(get("/api/emails/templates/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("welcome_email"));
    }

    @Test
    void testGetTemplates_Success() throws Exception {
        // Given
        String type = "WELCOME";
        Long organizationId = 100L;
        List<EmailTemplate> templates = Arrays.asList(createTestTemplate());
        when(templateService.getTemplatesByTypeAndOrganization(type, organizationId)).thenReturn(templates);

        // When & Then
        mockMvc.perform(get("/api/emails/templates")
                        .param("type", type)
                        .param("organizationId", organizationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateTemplate_Success() throws Exception {
        // Given
        Long id = 1L;
        EmailTemplate updateDetails = createTestTemplate();
        updateDetails.setName("Updated Name");
        EmailTemplate updated = createTestTemplate();
        updated.setName("Updated Name");

        when(templateService.updateTemplate(eq(id), any(EmailTemplate.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/emails/templates/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testDeleteTemplate_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(templateService).deleteTemplate(id);

        // When & Then
        mockMvc.perform(delete("/api/emails/templates/{id}", id))
                .andExpect(status().isNoContent());
    }
}

