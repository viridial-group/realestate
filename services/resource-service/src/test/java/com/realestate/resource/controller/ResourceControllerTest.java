package com.realestate.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.resource.entity.Domain;
import com.realestate.resource.entity.Resource;
import com.realestate.resource.service.ResourceService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ResourceController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Resource createTestResource() {
        Domain domain = new Domain();
        domain.setId(1L);
        domain.setName("Real Estate");

        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Test Resource");
        resource.setDescription("Test Description");
        resource.setDomain(domain);
        resource.setOrganizationId(100L);
        resource.setCreatedBy(1L);
        resource.setActive(true);
        resource.setShared(false);
        return resource;
    }

    @Test
    void testCreateResource_Success() throws Exception {
        // Given
        Resource resource = createTestResource();
        when(resourceService.createResource(any(Resource.class))).thenReturn(resource);

        // When & Then
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Resource"));
    }

    @Test
    void testGetResourceById_Success() throws Exception {
        // Given
        Long id = 1L;
        Resource resource = createTestResource();
        when(resourceService.getResourceById(id)).thenReturn(Optional.of(resource));

        // When & Then
        mockMvc.perform(get("/api/resources/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Resource"));
    }

    @Test
    void testGetResourcesByOrganizationId_Success() throws Exception {
        // Given
        Long organizationId = 100L;
        List<Resource> resources = Arrays.asList(createTestResource());
        when(resourceService.getResourcesByOrganizationId(organizationId)).thenReturn(resources);

        // When & Then
        mockMvc.perform(get("/api/resources")
                        .param("organizationId", organizationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetAllSharedResources_Success() throws Exception {
        // Given
        List<Resource> sharedResources = Arrays.asList(createTestResource());
        when(resourceService.getAllSharedResources()).thenReturn(sharedResources);

        // When & Then
        mockMvc.perform(get("/api/resources")
                        .param("shared", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateResource_Success() throws Exception {
        // Given
        Long id = 1L;
        Resource updateDetails = new Resource();
        updateDetails.setName("Updated Resource");
        Resource updated = createTestResource();
        updated.setName("Updated Resource");

        when(resourceService.updateResource(eq(id), any(Resource.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/resources/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Resource"));
    }

    @Test
    void testDeleteResource_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(resourceService).deleteResource(id);

        // When & Then
        mockMvc.perform(delete("/api/resources/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testShareResource_Success() throws Exception {
        // Given
        Long id = 1L;
        Resource shared = createTestResource();
        shared.setShared(true);

        when(resourceService.shareResourceWithOrganization(eq(id), anyLong(), any(), any(), any()))
                .thenReturn(shared);

        // When & Then
        mockMvc.perform(post("/api/resources/{id}/share", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationId\": 200, \"canRead\": true, \"canWrite\": true, \"canDelete\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shared").value(true));
    }
}

