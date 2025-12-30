package com.realestate.workflow.service;

import com.realestate.workflow.entity.ApprovalWorkflow;
import com.realestate.workflow.repository.ApprovalWorkflowRepository;
import com.realestate.workflow.repository.TaskRepository;
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
class WorkflowServiceTest {

    @Mock
    private ApprovalWorkflowRepository workflowRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private WorkflowService workflowService;

    private ApprovalWorkflow testWorkflow;

    @BeforeEach
    void setUp() {
        testWorkflow = new ApprovalWorkflow();
        testWorkflow.setId(1L);
        testWorkflow.setName("Property Approval Workflow");
        testWorkflow.setDescription("Workflow for approving properties");
        testWorkflow.setAction("PROPERTY_CREATE");
        testWorkflow.setTargetType("PROPERTY");
        testWorkflow.setOrganizationId(100L);
        testWorkflow.setSteps("[{\"step\":1,\"name\":\"Manager Review\"},{\"step\":2,\"name\":\"Admin Approval\"}]");
        testWorkflow.setRequiredRoles("[{\"step\":1,\"roles\":[\"MANAGER\"]},{\"step\":2,\"roles\":[\"ADMIN\"]}]");
        testWorkflow.setActive(true);
        testWorkflow.setIsDefault(true);
    }

    @Test
    void testCreateWorkflow_Success() {
        // Given
        when(workflowRepository.save(any(ApprovalWorkflow.class))).thenReturn(testWorkflow);

        // When
        ApprovalWorkflow result = workflowService.createWorkflow(testWorkflow);

        // Then
        assertNotNull(result);
        assertEquals(testWorkflow.getName(), result.getName());
        verify(workflowRepository).save(testWorkflow);
    }

    @Test
    void testGetWorkflowById_Success() {
        // Given
        Long id = 1L;
        when(workflowRepository.findById(id)).thenReturn(Optional.of(testWorkflow));

        // When
        Optional<ApprovalWorkflow> result = workflowService.getWorkflowById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testWorkflow.getId(), result.get().getId());
        verify(workflowRepository).findById(id);
    }

    @Test
    void testGetWorkflowsByOrganizationId_Success() {
        // Given
        Long organizationId = 100L;
        List<ApprovalWorkflow> workflows = Arrays.asList(testWorkflow);
        when(workflowRepository.findByOrganizationId(organizationId)).thenReturn(workflows);

        // When
        List<ApprovalWorkflow> result = workflowService.getWorkflowsByOrganizationId(organizationId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(workflowRepository).findByOrganizationId(organizationId);
    }

    @Test
    void testGetWorkflowsByOrganizationAndAction_Success() {
        // Given
        Long organizationId = 100L;
        String action = "PROPERTY_CREATE";
        List<ApprovalWorkflow> workflows = Arrays.asList(testWorkflow);
        when(workflowRepository.findActiveByOrganizationIdAndAction(organizationId, action)).thenReturn(workflows);

        // When
        List<ApprovalWorkflow> result = workflowService.getWorkflowsByOrganizationAndAction(organizationId, action);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(workflowRepository).findActiveByOrganizationIdAndAction(organizationId, action);
    }

    @Test
    void testGetDefaultWorkflow_Success() {
        // Given
        Long organizationId = 100L;
        String action = "PROPERTY_CREATE";
        when(workflowRepository.findDefaultByOrganizationIdAndAction(organizationId, action))
                .thenReturn(Optional.of(testWorkflow));

        // When
        Optional<ApprovalWorkflow> result = workflowService.getDefaultWorkflow(organizationId, action);

        // Then
        assertTrue(result.isPresent());
        assertTrue(result.get().getIsDefault());
        verify(workflowRepository).findDefaultByOrganizationIdAndAction(organizationId, action);
    }

    @Test
    void testUpdateWorkflow_Success() {
        // Given
        Long id = 1L;
        ApprovalWorkflow updateDetails = new ApprovalWorkflow();
        updateDetails.setName("Updated Workflow");
        updateDetails.setDescription("Updated Description");

        when(workflowRepository.findById(id)).thenReturn(Optional.of(testWorkflow));
        when(workflowRepository.save(any(ApprovalWorkflow.class))).thenReturn(testWorkflow);

        // When
        ApprovalWorkflow result = workflowService.updateWorkflow(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Workflow", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(workflowRepository).findById(id);
        verify(workflowRepository).save(testWorkflow);
    }

    @Test
    void testUpdateWorkflow_NotFound() {
        // Given
        Long id = 999L;
        ApprovalWorkflow updateDetails = new ApprovalWorkflow();
        when(workflowRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> workflowService.updateWorkflow(id, updateDetails));
        verify(workflowRepository).findById(id);
        verify(workflowRepository, never()).save(any(ApprovalWorkflow.class));
    }

    @Test
    void testDeleteWorkflow_Success() {
        // Given
        Long id = 1L;
        when(workflowRepository.existsById(id)).thenReturn(true);
        doNothing().when(workflowRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> workflowService.deleteWorkflow(id));

        // Then
        verify(workflowRepository).existsById(id);
        verify(workflowRepository).deleteById(id);
    }

    @Test
    void testStartWorkflow_Success() {
        // Given
        Long workflowId = 1L;
        Long targetId = 100L;
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(testWorkflow));
        when(workflowRepository.save(any(ApprovalWorkflow.class))).thenReturn(testWorkflow);

        // When
        ApprovalWorkflow result = workflowService.startWorkflow(workflowId, targetId);

        // Then
        assertNotNull(result);
        assertEquals(targetId, result.getTargetId());
        verify(workflowRepository).findById(workflowId);
        verify(workflowRepository).save(testWorkflow);
    }
}

