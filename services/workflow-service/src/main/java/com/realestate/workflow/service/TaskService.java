package com.realestate.workflow.service;

import com.realestate.common.event.WorkflowTaskCompletedEvent;
import com.realestate.common.event.WorkflowTaskCreatedEvent;
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
    private final WorkflowEventProducer eventProducer;

    public TaskService(TaskRepository taskRepository, WorkflowEventProducer eventProducer) {
        this.taskRepository = taskRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public Task createTask(Task task) {
        Task saved = taskRepository.save(task);
        
        // Publish event to Kafka
        if (saved.getWorkflow() != null) {
            WorkflowTaskCreatedEvent event = new WorkflowTaskCreatedEvent(
                    saved.getWorkflow().getOrganizationId(),
                    saved.getWorkflow().getCreatedBy(),
                    saved.getId(),
                    saved.getWorkflow().getId(),
                    saved.getType(),
                    saved.getStatus(),
                    saved.getAssignedTo(),
                    saved.getAssignedRole(),
                    saved.getWorkflow().getTargetType(),
                    saved.getWorkflow().getTargetId()
            );
            eventProducer.publishTaskCreated(event);
        }
        
        return saved;
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

        Task saved = taskRepository.save(task);
        
        // Publish event to Kafka
        if (saved.getWorkflow() != null) {
            WorkflowTaskCompletedEvent event = new WorkflowTaskCompletedEvent(
                    saved.getWorkflow().getOrganizationId(),
                    completedBy,
                    saved.getId(),
                    saved.getWorkflow().getId(),
                    "APPROVED",
                    comments,
                    completedBy,
                    saved.getWorkflow().getTargetType(),
                    saved.getWorkflow().getTargetId()
            );
            eventProducer.publishTaskCompleted(event);
        }
        
        return saved;
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

        Task saved = taskRepository.save(task);
        
        // Publish event to Kafka
        if (saved.getWorkflow() != null) {
            WorkflowTaskCompletedEvent event = new WorkflowTaskCompletedEvent(
                    saved.getWorkflow().getOrganizationId(),
                    completedBy,
                    saved.getId(),
                    saved.getWorkflow().getId(),
                    "REJECTED",
                    comments,
                    completedBy,
                    saved.getWorkflow().getTargetType(),
                    saved.getWorkflow().getTargetId()
            );
            eventProducer.publishTaskCompleted(event);
        }
        
        return saved;
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}

