package com.realestate.workflow.mapper;

import com.realestate.workflow.dto.TaskDTO;
import com.realestate.workflow.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStepNumber(task.getStepNumber());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setAssignedRole(task.getAssignedRole());
        dto.setStatus(task.getStatus());
        dto.setDueDate(task.getDueDate());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setCompletedBy(task.getCompletedBy());
        dto.setComments(task.getComments());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        
        // Mapper le workflow (évite lazy loading)
        if (task.getWorkflow() != null) {
            dto.setWorkflowId(task.getWorkflow().getId());
            dto.setWorkflowName(task.getWorkflow().getName());
        }
        
        return dto;
    }

    public Task toEntity(TaskDTO dto) {
        if (dto == null) {
            return null;
        }
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStepNumber(dto.getStepNumber());
        task.setAssignedTo(dto.getAssignedTo());
        task.setAssignedRole(dto.getAssignedRole());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        task.setCompletedAt(dto.getCompletedAt());
        task.setCompletedBy(dto.getCompletedBy());
        task.setComments(dto.getComments());
        // Note: workflow doit être géré séparément
        return task;
    }
}

