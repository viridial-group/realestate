package com.realestate.workflow.controller;

import com.realestate.workflow.entity.Task;
import com.realestate.workflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/workflows/tasks")
@Tag(name = "Tasks", description = "Task management API for workflow tasks and approvals")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create task", description = "Creates a new task in a workflow")
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task created = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Returns task information for a specific task ID")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List tasks", description = "Returns a list of tasks filtered by workflow, assigned user, or status")
    public ResponseEntity<List<Task>> getTasks(
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

        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates task information for a specific task ID")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody Task taskDetails) {
        try {
            Task updated = taskService.updateTask(id, taskDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve task", description = "Approves a task and marks it as completed")
    public ResponseEntity<Task> approveTask(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Long completedBy = Long.valueOf(request.get("completedBy").toString());
            String comments = request.containsKey("comments") ? request.get("comments").toString() : null;
            
            Task approved = taskService.approveTask(id, completedBy, comments);
            return ResponseEntity.ok(approved);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject task", description = "Rejects a task and marks it as rejected")
    public ResponseEntity<Task> rejectTask(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Long completedBy = Long.valueOf(request.get("completedBy").toString());
            String comments = request.containsKey("comments") ? request.get("comments").toString() : null;
            
            Task rejected = taskService.rejectTask(id, completedBy, comments);
            return ResponseEntity.ok(rejected);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a task from the database by ID")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

