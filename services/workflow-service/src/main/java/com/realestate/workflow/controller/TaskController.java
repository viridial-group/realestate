package com.realestate.workflow.controller;

import com.realestate.workflow.dto.TaskDTO;
import com.realestate.workflow.entity.Task;
import com.realestate.workflow.mapper.TaskMapper;
import com.realestate.workflow.service.TaskService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflows/tasks")
@Tag(name = "Tasks", description = "Task management API for workflow tasks and approvals")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @PostMapping
    @Operation(summary = "Create task", description = "Creates a new task in a workflow")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        Task created = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Returns task information for a specific task ID")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        return ResponseEntity.ok(taskMapper.toDTO(task));
    }

    @GetMapping
    @Operation(summary = "List tasks", description = "Returns a list of tasks filtered by workflow, assigned user, or status")
    public ResponseEntity<List<TaskDTO>> getTasks(
            @RequestParam(required = false) Long workflowId,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) Boolean overdue) {
        List<Task> tasks;

        if (workflowId != null) {
            tasks = taskService.getTasksByWorkflowId(workflowId);
        } else if (assignedTo != null) {
            if (overdue != null && overdue) {
                tasks = taskService.getOverdueTasks()
                        .stream()
                        .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().equals(assignedTo))
                        .toList();
            } else {
                tasks = taskService.getPendingTasksByAssignedTo(assignedTo);
            }
        } else if (overdue != null && overdue) {
            tasks = taskService.getOverdueTasks();
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<TaskDTO> taskDTOs = tasks.stream()
                .map(taskMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates task information for a specific task ID")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        Task updated = taskService.updateTask(id, task);
        return ResponseEntity.ok(taskMapper.toDTO(updated));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve task", description = "Approves a task and marks it as completed")
    public ResponseEntity<TaskDTO> approveTask(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long completedBy = Long.valueOf(request.get("completedBy").toString());
        String comments = request.containsKey("comments") ? request.get("comments").toString() : null;
        
        Task approved = taskService.approveTask(id, completedBy, comments);
        return ResponseEntity.ok(taskMapper.toDTO(approved));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject task", description = "Rejects a task and marks it as rejected")
    public ResponseEntity<TaskDTO> rejectTask(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long completedBy = Long.valueOf(request.get("completedBy").toString());
        String comments = request.containsKey("comments") ? request.get("comments").toString() : null;
        
        Task rejected = taskService.rejectTask(id, completedBy, comments);
        return ResponseEntity.ok(taskMapper.toDTO(rejected));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a task from the database by ID")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
