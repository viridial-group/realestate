package com.realestate.organization.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.organization.entity.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrganizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndRetrieveOrganization() throws Exception {
        // Create organization
        Organization organization = new Organization();
        organization.setName("Test Organization");
        organization.setDescription("Test Description");
        organization.setDomain("test.com");

        MvcResult createResult = mockMvc.perform(post("/api/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organization)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Organization"))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Organization createdOrg = objectMapper.readValue(response, Organization.class);
        assertNotNull(createdOrg.getId());

        // Retrieve organization
        mockMvc.perform(get("/api/organizations/{id}", createdOrg.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdOrg.getId()))
                .andExpect(jsonPath("$.name").value("Test Organization"));
    }

    @Test
    void testUpdateOrganization() throws Exception {
        // Create organization first
        Organization organization = new Organization();
        organization.setName("Original Name");
        organization.setDescription("Original Description");

        MvcResult createResult = mockMvc.perform(post("/api/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organization)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Organization createdOrg = objectMapper.readValue(response, Organization.class);

        // Update organization
        Organization updateDetails = new Organization();
        updateDetails.setName("Updated Name");
        updateDetails.setDescription("Updated Description");

        mockMvc.perform(put("/api/organizations/{id}", createdOrg.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }
}

