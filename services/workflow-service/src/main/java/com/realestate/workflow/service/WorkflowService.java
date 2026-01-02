package com.realestate.workflow.service;

import com.realestate.workflow.entity.ApprovalWorkflow;
import com.realestate.workflow.entity.Task;
import com.realestate.workflow.repository.ApprovalWorkflowRepository;
import com.realestate.workflow.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WorkflowService {

    private final ApprovalWorkflowRepository workflowRepository;
    private final TaskRepository taskRepository;

    public WorkflowService(
            ApprovalWorkflowRepository workflowRepository,
            TaskRepository taskRepository) {
        this.workflowRepository = workflowRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public ApprovalWorkflow createWorkflow(ApprovalWorkflow workflow) {
        return workflowRepository.save(workflow);
    }

    @Transactional(readOnly = true)
    public Optional<ApprovalWorkflow> getWorkflowById(Long id) {
        return workflowRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ApprovalWorkflow> getWorkflowsByOrganizationId(Long organizationId) {
        return workflowRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public List<ApprovalWorkflow> getWorkflowsByAction(String action) {
        return workflowRepository.findByAction(action);
    }

    @Transactional(readOnly = true)
    public List<ApprovalWorkflow> getWorkflowsByOrganizationAndAction(Long organizationId, String action) {
        return workflowRepository.findActiveByOrganizationIdAndAction(organizationId, action);
    }

    @Transactional(readOnly = true)
    public Optional<ApprovalWorkflow> getDefaultWorkflow(Long organizationId, String action) {
        return workflowRepository.findDefaultByOrganizationIdAndAction(organizationId, action);
    }

    @Transactional(readOnly = true)
    public List<ApprovalWorkflow> getWorkflowsByTarget(String targetType, Long targetId) {
        return workflowRepository.findActiveByTarget(targetType, targetId);
    }

    @Transactional
    public ApprovalWorkflow updateWorkflow(Long id, ApprovalWorkflow workflowDetails) {
        ApprovalWorkflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found with id: " + id));

        if (workflowDetails.getName() != null) {
            workflow.setName(workflowDetails.getName());
        }
        if (workflowDetails.getDescription() != null) {
            workflow.setDescription(workflowDetails.getDescription());
        }
        if (workflowDetails.getSteps() != null) {
            workflow.setSteps(workflowDetails.getSteps());
        }
        if (workflowDetails.getRequiredRoles() != null) {
            workflow.setRequiredRoles(workflowDetails.getRequiredRoles());
        }
        if (workflowDetails.getActive() != null) {
            workflow.setActive(workflowDetails.getActive());
        }
        if (workflowDetails.getIsDefault() != null) {
            workflow.setIsDefault(workflowDetails.getIsDefault());
        }
        if (workflowDetails.getStatus() != null) {
            workflow.setStatus(workflowDetails.getStatus());
        }

        return workflowRepository.save(workflow);
    }

    @Transactional
    public void deleteWorkflow(Long id) {
        if (!workflowRepository.existsById(id)) {
            throw new RuntimeException("Workflow not found with id: " + id);
        }
        workflowRepository.deleteById(id);
    }

    @Transactional
    public ApprovalWorkflow startWorkflow(Long workflowId, Long targetId) {
        ApprovalWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found with id: " + workflowId));

        workflow.setTargetId(targetId);
        return workflowRepository.save(workflow);
    }
}

