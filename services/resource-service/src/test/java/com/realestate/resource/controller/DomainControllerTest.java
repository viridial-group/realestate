package com.realestate.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.resource.entity.Domain;
import com.realestate.resource.service.DomainService;
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

@WebMvcTest(controllers = DomainController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class DomainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DomainService domainService;

    @Autowired
    private ObjectMapper objectMapper;

    private Domain createTestDomain() {
        Domain domain = new Domain();
        domain.setId(1L);
        domain.setName("Real Estate");
        domain.setDescription("Real estate properties domain");
        domain.setActive(true);
        return domain;
    }

    @Test
    void testCreateDomain_Success() throws Exception {
        // Given
        Domain domain = createTestDomain();
        when(domainService.createDomain(any(Domain.class))).thenReturn(domain);

        // When & Then
        mockMvc.perform(post("/api/resources/domains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(domain)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Real Estate"));
    }

    @Test
    void testGetDomainById_Success() throws Exception {
        // Given
        Long id = 1L;
        Domain domain = createTestDomain();
        when(domainService.getDomainById(id)).thenReturn(Optional.of(domain));

        // When & Then
        mockMvc.perform(get("/api/resources/domains/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Real Estate"));
    }

    @Test
    void testGetDomainById_NotFound() throws Exception {
        // Given
        Long id = 999L;
        when(domainService.getDomainById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/resources/domains/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllDomains_Success() throws Exception {
        // Given
        List<Domain> domains = Arrays.asList(createTestDomain());
        when(domainService.getAllDomains()).thenReturn(domains);

        // When & Then
        mockMvc.perform(get("/api/resources/domains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateDomain_Success() throws Exception {
        // Given
        Long id = 1L;
        Domain updateDetails = new Domain();
        updateDetails.setName("Updated Domain");
        Domain updated = createTestDomain();
        updated.setName("Updated Domain");

        when(domainService.updateDomain(eq(id), any(Domain.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/resources/domains/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Domain"));
    }

    @Test
    void testDeleteDomain_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(domainService).deleteDomain(id);

        // When & Then
        mockMvc.perform(delete("/api/resources/domains/{id}", id))
                .andExpect(status().isNoContent());
    }
}

