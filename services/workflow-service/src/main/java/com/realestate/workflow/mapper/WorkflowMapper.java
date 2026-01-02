package com.realestate.workflow.mapper;

import com.realestate.workflow.dto.WorkflowDTO;
import com.realestate.workflow.entity.ApprovalWorkflow;
import org.springframework.stereotype.Component;

@Component
public class WorkflowMapper {

    public WorkflowDTO toDTO(ApprovalWorkflow workflow) {
        if (workflow == null) {
            return null;
        }
        WorkflowDTO dto = new WorkflowDTO();
        dto.setId(workflow.getId());
        dto.setName(workflow.getName());
        dto.setDescription(workflow.getDescription());
        dto.setAction(workflow.getAction());
        dto.setOrganizationId(workflow.getOrganizationId());
        dto.setTargetType(workflow.getTargetType());
        dto.setTargetId(workflow.getTargetId());
        dto.setSteps(workflow.getSteps());
        dto.setRequiredRoles(workflow.getRequiredRoles());
        dto.setActive(workflow.getActive());
        dto.setIsDefault(workflow.getIsDefault());
        dto.setStatus(workflow.getStatus());
        dto.setCreatedAt(workflow.getCreatedAt());
        dto.setUpdatedAt(workflow.getUpdatedAt());
        return dto;
    }

    public ApprovalWorkflow toEntity(WorkflowDTO dto) {
        if (dto == null) {
            return null;
        }
        ApprovalWorkflow workflow = new ApprovalWorkflow();
        workflow.setId(dto.getId());
        workflow.setName(dto.getName());
        workflow.setDescription(dto.getDescription());
        workflow.setAction(dto.getAction());
        workflow.setOrganizationId(dto.getOrganizationId());
        workflow.setTargetType(dto.getTargetType());
        workflow.setTargetId(dto.getTargetId());
        workflow.setSteps(dto.getSteps());
        workflow.setRequiredRoles(dto.getRequiredRoles());
        workflow.setActive(dto.getActive());
        workflow.setIsDefault(dto.getIsDefault());
        if (dto.getStatus() != null) {
            workflow.setStatus(dto.getStatus());
        }
        return workflow;
    }
}

