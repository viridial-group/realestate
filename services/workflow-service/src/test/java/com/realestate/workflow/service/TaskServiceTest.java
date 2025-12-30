package com.realestate.workflow.service;

import com.realestate.workflow.entity.ApprovalWorkflow;
import com.realestate.workflow.entity.Task;
import com.realestate.workflow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private ApprovalWorkflow testWorkflow;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testWorkflow = new ApprovalWorkflow();
        testWorkflow.setId(1L);
        testWorkflow.setName("Property Approval Workflow");
        testWorkflow.setAction("PROPERTY_CREATE");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setWorkflow(testWorkflow);
        testTask.setTitle("Manager Review");
        testTask.setDescription("Review property details");
        testTask.setStepNumber(1);
        testTask.setAssignedTo(10L);
        testTask.setAssignedRole("MANAGER");
        testTask.setStatus("PENDING");
        testTask.setOrganizationId(100L);
        testTask.setActive(true);
    }

    @Test
    void testCreateTask_Success() {
        // Given
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.createTask(testTask);

        // Then
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(taskRepository).save(testTask);
    }

    @Test
    void testGetTaskById_Success() {
        // Given
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));

        // When
        Optional<Task> result = taskService.getTaskById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTask.getId(), result.get().getId());
        verify(taskRepository).findById(id);
    }

    @Test
    void testGetTasksByWorkflowId_Success() {
        // Given
        Long workflowId = 1L;
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findActiveByWorkflowIdOrderByStepNumber(workflowId)).thenReturn(tasks);

        // When
        List<Task> result = taskService.getTasksByWorkflowId(workflowId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository).findActiveByWorkflowIdOrderByStepNumber(workflowId);
    }

    @Test
    void testGetPendingTasksByAssignedTo_Success() {
        // Given
        Long assignedTo = 10L;
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findPendingTasksByAssignedTo(assignedTo)).thenReturn(tasks);

        // When
        List<Task> result = taskService.getPendingTasksByAssignedTo(assignedTo);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository).findPendingTasksByAssignedTo(assignedTo);
    }

    @Test
    void testApproveTask_Success() {
        // Given
        Long id = 1L;
        Long completedBy = 10L;
        String comments = "Approved";

        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.approveTask(id, completedBy, comments);

        // Then
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        assertEquals(completedBy, result.getCompletedBy());
        assertNotNull(result.getCompletedAt());
        verify(taskRepository).findById(id);
        verify(taskRepository).save(testTask);
    }

    @Test
    void testRejectTask_Success() {
        // Given
        Long id = 1L;
        Long completedBy = 10L;
        String comments = "Rejected - missing information";

        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.rejectTask(id, completedBy, comments);

        // Then
        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        assertEquals(completedBy, result.getCompletedBy());
        assertNotNull(result.getCompletedAt());
        verify(taskRepository).findById(id);
        verify(taskRepository).save(testTask);
    }

    @Test
    void testUpdateTask_Success() {
        // Given
        Long id = 1L;
        Task updateDetails = new Task();
        updateDetails.setTitle("Updated Title");
        updateDetails.setStatus("IN_PROGRESS");

        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.updateTask(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("IN_PROGRESS", result.getStatus());
        verify(taskRepository).findById(id);
        verify(taskRepository).save(testTask);
    }

    @Test
    void testDeleteTask_Success() {
        // Given
        Long id = 1L;
        when(taskRepository.existsById(id)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> taskService.deleteTask(id));

        // Then
        verify(taskRepository).existsById(id);
        verify(taskRepository).deleteById(id);
    }
}

