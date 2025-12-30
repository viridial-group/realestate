package com.realestate.workflow.service;

import com.realestate.workflow.entity.Task;
import com.realestate.workflow.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByWorkflowId(Long workflowId) {
        return taskRepository.findActiveByWorkflowIdOrderByStepNumber(workflowId);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByAssignedTo(Long assignedTo) {
        return taskRepository.findByAssignedTo(assignedTo);
    }

    @Transactional(readOnly = true)
    public List<Task> getPendingTasksByAssignedTo(Long assignedTo) {
        return taskRepository.findPendingTasksByAssignedTo(assignedTo);
    }

    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now());
    }

    @Transactional
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        if (taskDetails.getTitle() != null) {
            task.setTitle(taskDetails.getTitle());
        }
        if (taskDetails.getDescription() != null) {
            task.setDescription(taskDetails.getDescription());
        }
        if (taskDetails.getAssignedTo() != null) {
            task.setAssignedTo(taskDetails.getAssignedTo());
        }
        if (taskDetails.getAssignedRole() != null) {
            task.setAssignedRole(taskDetails.getAssignedRole());
        }
        if (taskDetails.getStatus() != null) {
            task.setStatus(taskDetails.getStatus());
        }
        if (taskDetails.getDueDate() != null) {
            task.setDueDate(taskDetails.getDueDate());
        }
        if (taskDetails.getComments() != null) {
            task.setComments(taskDetails.getComments());
        }

        return taskRepository.save(task);
    }

    @Transactional
    public Task approveTask(Long id, Long completedBy, String comments) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setStatus("APPROVED");
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletedBy(completedBy);
        if (comments != null) {
            task.setComments(comments);
        }

        return taskRepository.save(task);
    }

    @Transactional
    public Task rejectTask(Long id, Long completedBy, String comments) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setStatus("REJECTED");
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletedBy(completedBy);
        if (comments != null) {
            task.setComments(comments);
        }

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}

