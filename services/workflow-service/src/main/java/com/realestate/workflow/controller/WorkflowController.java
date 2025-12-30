package com.realestate.workflow.controller;

import com.realestate.workflow.entity.ApprovalWorkflow;
import com.realestate.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workflows")
@Tag(name = "Workflows", description = "Workflow and approval engine API for managing multi-step workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    @Operation(summary = "Create workflow", description = "Creates a new approval workflow")
    public ResponseEntity<ApprovalWorkflow> createWorkflow(@Valid @RequestBody ApprovalWorkflow workflow) {
        ApprovalWorkflow created = workflowService.createWorkflow(workflow);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workflow by ID", description = "Returns workflow information for a specific workflow ID")
    public ResponseEntity<ApprovalWorkflow> getWorkflowById(@PathVariable Long id) {
        return workflowService.getWorkflowById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List workflows", description = "Returns a list of workflows filtered by organization, action, or target")
    public ResponseEntity<List<ApprovalWorkflow>> getWorkflows(
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

        return ResponseEntity.ok(workflows);
    }

    @GetMapping("/default")
    @Operation(summary = "Get default workflow", description = "Returns the default workflow for an organization and action")
    public ResponseEntity<ApprovalWorkflow> getDefaultWorkflow(
            @RequestParam Long organizationId,
            @RequestParam String action) {
        return workflowService.getDefaultWorkflow(organizationId, action)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update workflow", description = "Updates workflow information for a specific workflow ID")
    public ResponseEntity<ApprovalWorkflow> updateWorkflow(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalWorkflow workflowDetails) {
        try {
            ApprovalWorkflow updated = workflowService.updateWorkflow(id, workflowDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start workflow", description = "Starts a workflow for a specific target")
    public ResponseEntity<ApprovalWorkflow> startWorkflow(
            @PathVariable Long id,
            @RequestParam Long targetId) {
        try {
            ApprovalWorkflow started = workflowService.startWorkflow(id, targetId);
            return ResponseEntity.ok(started);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete workflow", description = "Deletes a workflow from the database by ID")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        try {
            workflowService.deleteWorkflow(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

