package com.realestate.workflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.workflow.entity.ApprovalWorkflow;
import com.realestate.workflow.entity.Task;
import com.realestate.workflow.service.TaskService;
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

@WebMvcTest(controllers = TaskController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task createTestTask() {
        ApprovalWorkflow workflow = new ApprovalWorkflow();
        workflow.setId(1L);
        workflow.setName("Property Approval Workflow");

        Task task = new Task();
        task.setId(1L);
        task.setWorkflow(workflow);
        task.setTitle("Manager Review");
        task.setDescription("Review property details");
        task.setStepNumber(1);
        task.setAssignedTo(10L);
        task.setAssignedRole("MANAGER");
        task.setStatus("PENDING");
        task.setOrganizationId(100L);
        task.setActive(true);
        return task;
    }

    @Test
    void testCreateTask_Success() throws Exception {
        // Given
        Task task = createTestTask();
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        // When & Then
        mockMvc.perform(post("/api/workflows/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Manager Review"));
    }

    @Test
    void testGetTaskById_Success() throws Exception {
        // Given
        Long id = 1L;
        Task task = createTestTask();
        when(taskService.getTaskById(id)).thenReturn(Optional.of(task));

        // When & Then
        mockMvc.perform(get("/api/workflows/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Manager Review"));
    }

    @Test
    void testGetTasksByWorkflowId_Success() throws Exception {
        // Given
        Long workflowId = 1L;
        List<Task> tasks = Arrays.asList(createTestTask());
        when(taskService.getTasksByWorkflowId(workflowId)).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/workflows/tasks")
                        .param("workflowId", workflowId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetPendingTasksByAssignedTo_Success() throws Exception {
        // Given
        Long assignedTo = 10L;
        List<Task> tasks = Arrays.asList(createTestTask());
        when(taskService.getPendingTasksByAssignedTo(assignedTo)).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/workflows/tasks")
                        .param("assignedTo", assignedTo.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testApproveTask_Success() throws Exception {
        // Given
        Long id = 1L;
        Task approved = createTestTask();
        approved.setStatus("APPROVED");
        when(taskService.approveTask(eq(id), eq(10L), any())).thenReturn(approved);

        // When & Then
        mockMvc.perform(post("/api/workflows/tasks/{id}/approve", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"completedBy\":10,\"comments\":\"Approved\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void testRejectTask_Success() throws Exception {
        // Given
        Long id = 1L;
        Task rejected = createTestTask();
        rejected.setStatus("REJECTED");
        when(taskService.rejectTask(eq(id), eq(10L), any())).thenReturn(rejected);

        // When & Then
        mockMvc.perform(post("/api/workflows/tasks/{id}/reject", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"completedBy\":10,\"comments\":\"Rejected\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void testUpdateTask_Success() throws Exception {
        // Given
        Long id = 1L;
        Task updateDetails = createTestTask();
        updateDetails.setTitle("Updated Title");
        Task updated = createTestTask();
        updated.setTitle("Updated Title");

        when(taskService.updateTask(eq(id), any(Task.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/workflows/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void testDeleteTask_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(taskService).deleteTask(id);

        // When & Then
        mockMvc.perform(delete("/api/workflows/tasks/{id}", id))
                .andExpect(status().isNoContent());
    }
}

