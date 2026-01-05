package com.realestate.workflow.controller;

import com.realestate.workflow.dto.WorkflowDTO;
import com.realestate.workflow.entity.ApprovalWorkflow;
import com.realestate.workflow.mapper.WorkflowMapper;
import com.realestate.workflow.service.WorkflowService;
import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.common.client.IdentityServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflows")
@Tag(name = "Workflows", description = "Workflow and approval engine API for managing multi-step workflows")
public class WorkflowController {

    private final WorkflowService workflowService;
    private final WorkflowMapper workflowMapper;
    private final IdentityServiceClient identityServiceClient;

    public WorkflowController(
            WorkflowService workflowService, 
            WorkflowMapper workflowMapper,
            IdentityServiceClient identityServiceClient) {
        this.workflowService = workflowService;
        this.workflowMapper = workflowMapper;
        this.identityServiceClient = identityServiceClient;
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
    @Operation(summary = "List workflows", description = "Returns a list of workflows filtered by organization, action, or target. Automatically filters by user permissions and accessible organizations.")
    public ResponseEntity<List<WorkflowDTO>> getWorkflows(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Long targetId) {
        try {
            // Récupérer le contexte de permissions si un token est fourni
            Long userId = null;
            Set<Long> accessibleOrgIds = null;
            boolean isSuperAdmin = false;
            boolean isAdmin = false;
            
            if (authorization != null && authorization.startsWith("Bearer ") && identityServiceClient != null) {
                String token = authorization.substring(7);
                
                try {
                    java.util.Optional<com.realestate.common.client.dto.PermissionContextDTO> permissionContextOpt = 
                            identityServiceClient.getPermissionContext(token).block();
                    
                    if (permissionContextOpt.isPresent()) {
                        com.realestate.common.client.dto.PermissionContextDTO permissionContext = permissionContextOpt.get();
                        userId = permissionContext.getUserId();
                        isSuperAdmin = permissionContext.isSuperAdmin();
                        isAdmin = permissionContext.isAdmin();
                        accessibleOrgIds = permissionContext.getAccessibleOrganizationIds();
                    }
                } catch (Exception e) {
                    // Continuer sans contexte de permissions (peut être un endpoint public)
                }
            }
            
            List<ApprovalWorkflow> workflows;
            
            // Si l'utilisateur n'est pas super admin/admin, filtrer selon ses permissions
            if (!isSuperAdmin && !isAdmin && userId != null) {
                // Si un organizationId est spécifié, vérifier qu'il est accessible
                if (organizationId != null && accessibleOrgIds != null && !accessibleOrgIds.contains(organizationId)) {
                    // L'utilisateur n'a pas accès à cette organisation
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                
                // Utiliser le filtrage avec permissions
                workflows = workflowService.getWorkflowsWithPermissions(
                        userId, accessibleOrgIds, organizationId, action, targetType, targetId);
            } else {
                // Super admin ou admin : voir tous les workflows (comportement original)
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
            }

            List<WorkflowDTO> workflowDTOs = workflows.stream()
                    .map(workflowMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(workflowDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
