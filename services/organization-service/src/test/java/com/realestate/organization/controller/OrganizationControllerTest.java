package com.realestate.organization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.organization.entity.Organization;
import com.realestate.organization.service.OrganizationService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrganizationController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationService organizationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Organization createTestOrganization() {
        Organization org = new Organization();
        org.setId(1L);
        org.setName("Test Organization");
        org.setDescription("Test Description");
        org.setActive(true);
        return org;
    }

    @Test
    void testCreateOrganization_Success() throws Exception {
        // Given
        Organization organization = createTestOrganization();
        when(organizationService.createOrganization(any(Organization.class))).thenReturn(organization);

        // When & Then
        mockMvc.perform(post("/api/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organization)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Organization"));
    }

    @Test
    void testGetOrganizationById_Success() throws Exception {
        // Given
        Long id = 1L;
        Organization organization = createTestOrganization();
        when(organizationService.getOrganizationById(id)).thenReturn(Optional.of(organization));

        // When & Then
        mockMvc.perform(get("/api/organizations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Organization"));
    }

    @Test
    void testGetOrganizationById_NotFound() throws Exception {
        // Given
        Long id = 999L;
        when(organizationService.getOrganizationById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/organizations/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllOrganizations_Success() throws Exception {
        // Given
        List<Organization> organizations = Arrays.asList(createTestOrganization());
        when(organizationService.getAllOrganizations()).thenReturn(organizations);

        // When & Then
        mockMvc.perform(get("/api/organizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateOrganization_Success() throws Exception {
        // Given
        Long id = 1L;
        Organization updateDetails = new Organization();
        updateDetails.setName("Updated Name");
        Organization updated = createTestOrganization();
        updated.setName("Updated Name");

        when(organizationService.updateOrganization(id, updateDetails)).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/organizations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testDeleteOrganization_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(organizationService).deleteOrganization(id);

        // When & Then
        mockMvc.perform(delete("/api/organizations/{id}", id))
                .andExpect(status().isNoContent());
    }
}

