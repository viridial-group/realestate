package com.realestate.audit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.audit.entity.AuditLog;
import com.realestate.audit.service.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuditController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuditLog createTestAuditLog() {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(1L);
        auditLog.setActorId(10L);
        auditLog.setActorEmail("user@example.com");
        auditLog.setOrganizationId(100L);
        auditLog.setAction("CREATE");
        auditLog.setTargetType("PROPERTY");
        auditLog.setTargetId(50L);
        auditLog.setStatus("SUCCESS");
        auditLog.setDescription("Property created");
        auditLog.setIpAddress("192.168.1.1");
        return auditLog;
    }

    @Test
    void testCreateAuditLog_Success() throws Exception {
        // Given
        AuditLog auditLog = createTestAuditLog();
        when(auditService.createAuditLog(any(AuditLog.class))).thenReturn(auditLog);

        // When & Then
        mockMvc.perform(post("/api/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auditLog)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.action").value("CREATE"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testLogAction_Success() throws Exception {
        // Given
        AuditLog auditLog = createTestAuditLog();
        when(auditService.logAction(any(), any(), anyLong(), anyString(), any(), any(), anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn(auditLog);

        // When & Then
        mockMvc.perform(post("/api/audit/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"actorId\":10,\"actorEmail\":\"user@example.com\",\"organizationId\":100,\"action\":\"CREATE\",\"targetType\":\"PROPERTY\",\"targetId\":50}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.action").value("CREATE"));
    }

    @Test
    void testGetAuditLogById_Success() throws Exception {
        // Given
        Long id = 1L;
        AuditLog auditLog = createTestAuditLog();
        when(auditService.getAuditLogById(id)).thenReturn(Optional.of(auditLog));

        // When & Then
        mockMvc.perform(get("/api/audit/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.action").value("CREATE"));
    }

    @Test
    void testGetAuditLogs_Success() throws Exception {
        // Given
        Long organizationId = 100L;
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(createTestAuditLog()));
        when(auditService.getAuditLogsByOrganizationId(eq(organizationId), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/audit")
                        .param("organizationId", organizationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void testGetAuditLogsByTarget_Success() throws Exception {
        // Given
        String targetType = "PROPERTY";
        Long targetId = 50L;
        when(auditService.getAuditLogsByTarget(targetType, targetId))
                .thenReturn(Arrays.asList(createTestAuditLog()));

        // When & Then
        mockMvc.perform(get("/api/audit/target")
                        .param("targetType", targetType)
                        .param("targetId", targetId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}

