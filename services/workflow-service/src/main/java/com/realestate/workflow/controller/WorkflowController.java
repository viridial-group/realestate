package com.realestate.workflow.controller;

import com.realestate.workflow.dto.WorkflowDTO;
import com.realestate.workflow.entity.ApprovalWorkflow;
import com.realestate.workflow.mapper.WorkflowMapper;
import com.realestate.workflow.service.WorkflowService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflows")
@Tag(name = "Workflows", description = "Workflow and approval engine API for managing multi-step workflows")
public class WorkflowController {

    private final WorkflowService workflowService;
    private final WorkflowMapper workflowMapper;

    public WorkflowController(WorkflowService workflowService, WorkflowMapper workflowMapper) {
        this.workflowService = workflowService;
        this.workflowMapper = workflowMapper;
    }

    @PostMapping
    @Operation(summary = "Create workflow", description = "Creates a new approval workflow")
    public ResponseEntity<WorkflowDTO> createWorkflow(@Valid @RequestBody WorkflowDTO workflowDTO) {
        ApprovalWorkflow workflow = workflowMapper.toEntity(workflowDTO);
        ApprovalWorkflow created = workflowService.createWorkflow(workflow);
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workflow by ID", description = "Returns workflow information for a specific workflow ID")
    public ResponseEntity<WorkflowDTO> getWorkflowById(@PathVariable Long id) {
        ApprovalWorkflow workflow = workflowService.getWorkflowById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", id));
        return ResponseEntity.ok(workflowMapper.toDTO(workflow));
    }

    @GetMapping
    @Operation(summary = "List workflows", description = "Returns a list of workflows filtered by organization, action, or target")
    public ResponseEntity<List<WorkflowDTO>> getWorkflows(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Long targetId) {
        List<ApprovalWorkflow> workflows;

        if (organizationId != null && action != null) {
            workflows = workflowService.getWorkflowsByOrganizationAndAction(organizationId, action);
        } else if (organizationId != null) {
            workflows = workflowService.getWorkflowsByOrganizationId(organizationId);
        } else if (action != null) {
            workflows = workflowService.getWorkflowsByAction(action);
        } else if (targetType != null && targetId != null) {
            workflows = workflowService.getWorkflowsByTarget(targetType, targetId);
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<WorkflowDTO> workflowDTOs = workflows.stream()
                .map(workflowMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(workflowDTOs);
    }

    @GetMapping("/default")
    @Operation(summary = "Get default workflow", description = "Returns the default workflow for an organization and action")
    public ResponseEntity<WorkflowDTO> getDefaultWorkflow(
            @RequestParam Long organizationId,
            @RequestParam String action) {
        ApprovalWorkflow workflow = workflowService.getDefaultWorkflow(organizationId, action)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "default for organization " + organizationId + " and action " + action));
        return ResponseEntity.ok(workflowMapper.toDTO(workflow));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update workflow", description = "Updates workflow information for a specific workflow ID")
    public ResponseEntity<WorkflowDTO> updateWorkflow(
            @PathVariable Long id,
            @Valid @RequestBody WorkflowDTO workflowDTO) {
        ApprovalWorkflow workflow = workflowMapper.toEntity(workflowDTO);
        ApprovalWorkflow updated = workflowService.updateWorkflow(id, workflow);
        return ResponseEntity.ok(workflowMapper.toDTO(updated));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start workflow", description = "Starts a workflow for a specific target")
    public ResponseEntity<WorkflowDTO> startWorkflow(
            @PathVariable Long id,
            @RequestParam Long targetId) {
        ApprovalWorkflow started = workflowService.startWorkflow(id, targetId);
        return ResponseEntity.ok(workflowMapper.toDTO(started));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete workflow", description = "Deletes a workflow from the database by ID")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }
}
