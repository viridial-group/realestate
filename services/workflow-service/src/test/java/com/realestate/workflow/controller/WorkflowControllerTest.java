package com.realestate.workflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.workflow.entity.ApprovalWorkflow;
import com.realestate.workflow.service.WorkflowService;
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

@WebMvcTest(controllers = WorkflowController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class WorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkflowService workflowService;

    @Autowired
    private ObjectMapper objectMapper;

    private ApprovalWorkflow createTestWorkflow() {
        ApprovalWorkflow workflow = new ApprovalWorkflow();
        workflow.setId(1L);
        workflow.setName("Property Approval Workflow");
        workflow.setDescription("Workflow for approving properties");
        workflow.setAction("PROPERTY_CREATE");
        workflow.setTargetType("PROPERTY");
        workflow.setOrganizationId(100L);
        workflow.setSteps("[{\"step\":1,\"name\":\"Manager Review\"}]");
        workflow.setRequiredRoles("[{\"step\":1,\"roles\":[\"MANAGER\"]}]");
        workflow.setActive(true);
        workflow.setIsDefault(true);
        return workflow;
    }

    @Test
    void testCreateWorkflow_Success() throws Exception {
        // Given
        ApprovalWorkflow workflow = createTestWorkflow();
        when(workflowService.createWorkflow(any(ApprovalWorkflow.class))).thenReturn(workflow);

        // When & Then
        mockMvc.perform(post("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workflow)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Property Approval Workflow"))
                .andExpect(jsonPath("$.action").value("PROPERTY_CREATE"));
    }

    @Test
    void testGetWorkflowById_Success() throws Exception {
        // Given
        Long id = 1L;
        ApprovalWorkflow workflow = createTestWorkflow();
        when(workflowService.getWorkflowById(id)).thenReturn(Optional.of(workflow));

        // When & Then
        mockMvc.perform(get("/api/workflows/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Property Approval Workflow"));
    }

    @Test
    void testGetWorkflowsByOrganizationId_Success() throws Exception {
        // Given
        Long organizationId = 100L;
        List<ApprovalWorkflow> workflows = Arrays.asList(createTestWorkflow());
        when(workflowService.getWorkflowsByOrganizationId(organizationId)).thenReturn(workflows);

        // When & Then
        mockMvc.perform(get("/api/workflows")
                        .param("organizationId", organizationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetDefaultWorkflow_Success() throws Exception {
        // Given
        Long organizationId = 100L;
        String action = "PROPERTY_CREATE";
        ApprovalWorkflow workflow = createTestWorkflow();
        when(workflowService.getDefaultWorkflow(organizationId, action)).thenReturn(Optional.of(workflow));

        // When & Then
        mockMvc.perform(get("/api/workflows/default")
                        .param("organizationId", organizationId.toString())
                        .param("action", action))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    void testUpdateWorkflow_Success() throws Exception {
        // Given
        Long id = 1L;
        ApprovalWorkflow updateDetails = createTestWorkflow();
        updateDetails.setName("Updated Workflow");
        ApprovalWorkflow updated = createTestWorkflow();
        updated.setName("Updated Workflow");

        when(workflowService.updateWorkflow(eq(id), any(ApprovalWorkflow.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/workflows/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Workflow"));
    }

    @Test
    void testStartWorkflow_Success() throws Exception {
        // Given
        Long id = 1L;
        Long targetId = 100L;
        ApprovalWorkflow workflow = createTestWorkflow();
        workflow.setTargetId(targetId);
        when(workflowService.startWorkflow(id, targetId)).thenReturn(workflow);

        // When & Then
        mockMvc.perform(post("/api/workflows/{id}/start", id)
                        .param("targetId", targetId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetId").value(targetId));
    }

    @Test
    void testDeleteWorkflow_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(workflowService).deleteWorkflow(id);

        // When & Then
        mockMvc.perform(delete("/api/workflows/{id}", id))
                .andExpect(status().isNoContent());
    }
}

