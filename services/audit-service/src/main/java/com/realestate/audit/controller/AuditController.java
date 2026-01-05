package com.realestate.audit.controller;

import com.realestate.audit.dto.AuditLogDTO;
import com.realestate.audit.entity.AuditLog;
import com.realestate.audit.mapper.AuditLogMapper;
import com.realestate.audit.service.AuditService;
import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.common.client.IdentityServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit Logs", description = "Audit and logging API for complete traceability of all critical actions")
public class AuditController {

    private final AuditService auditService;
    private final AuditLogMapper auditLogMapper;
    private final IdentityServiceClient identityServiceClient;

    public AuditController(
            AuditService auditService, 
            AuditLogMapper auditLogMapper,
            IdentityServiceClient identityServiceClient) {
        this.auditService = auditService;
        this.auditLogMapper = auditLogMapper;
        this.identityServiceClient = identityServiceClient;
    }

    @PostMapping
    @Operation(summary = "Create audit log", description = "Creates a new audit log entry")
    public ResponseEntity<AuditLogDTO> createAuditLog(@Valid @RequestBody AuditLogDTO auditLogDTO) {
        AuditLog auditLog = auditLogMapper.toEntity(auditLogDTO);
        AuditLog created = auditService.createAuditLog(auditLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(auditLogMapper.toDTO(created));
    }

    @PostMapping("/log")
    @Operation(summary = "Log action", description = "Logs an action with all relevant details")
    public ResponseEntity<AuditLogDTO> logAction(@RequestBody Map<String, Object> request) {
        try {
            Long actorId = request.containsKey("actorId") ? Long.valueOf(request.get("actorId").toString()) : null;
            String actorEmail = request.containsKey("actorEmail") ? request.get("actorEmail").toString() : null;
            Long organizationId = Long.valueOf(request.get("organizationId").toString());
            String action = request.get("action").toString();
            String targetType = request.containsKey("targetType") ? request.get("targetType").toString() : null;
            Long targetId = request.containsKey("targetId") ? Long.valueOf(request.get("targetId").toString()) : null;
            String status = request.containsKey("status") ? request.get("status").toString() : "SUCCESS";
            String description = request.containsKey("description") ? request.get("description").toString() : null;
            String ipAddress = request.containsKey("ipAddress") ? request.get("ipAddress").toString() : null;
            String userAgent = request.containsKey("userAgent") ? request.get("userAgent").toString() : null;
            String requestMethod = request.containsKey("requestMethod") ? request.get("requestMethod").toString() : null;
            String requestPath = request.containsKey("requestPath") ? request.get("requestPath").toString() : null;
            String metadata = request.containsKey("metadata") ? request.get("metadata").toString() : null;

            AuditLog logged = auditService.logAction(
                    actorId, actorEmail, organizationId, action, targetType, targetId,
                    status, description, ipAddress, userAgent, requestMethod, requestPath, metadata);
            return ResponseEntity.status(HttpStatus.CREATED).body(auditLogMapper.toDTO(logged));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audit log by ID", description = "Returns audit log information for a specific audit log ID")
    public ResponseEntity<AuditLogDTO> getAuditLogById(@PathVariable Long id) {
        AuditLog auditLog = auditService.getAuditLogById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", id));
        return ResponseEntity.ok(auditLogMapper.toDTO(auditLog));
    }

    @GetMapping
    @Operation(summary = "List audit logs", description = "Returns a paginated list of audit logs with various filters. Automatically filters by user permissions and accessible organizations.")
    public ResponseEntity<Page<AuditLogDTO>> getAuditLogs(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // Récupérer le contexte de permissions si un token est fourni
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
                        isSuperAdmin = permissionContext.isSuperAdmin();
                        isAdmin = permissionContext.isAdmin();
                        accessibleOrgIds = permissionContext.getAccessibleOrganizationIds();
                    }
                } catch (Exception e) {
                    // Continuer sans contexte de permissions
                }
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditLog> auditLogs;
            
            // Si l'utilisateur n'est pas super admin/admin, filtrer selon ses permissions
            if (!isSuperAdmin && !isAdmin && accessibleOrgIds != null && !accessibleOrgIds.isEmpty()) {
                // Si un organizationId est spécifié, vérifier qu'il est accessible
                if (organizationId != null && !accessibleOrgIds.contains(organizationId)) {
                    // L'utilisateur n'a pas accès à cette organisation
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                
                // Utiliser le filtrage avec permissions
                auditLogs = auditService.getAuditLogsWithPermissions(
                        accessibleOrgIds, organizationId, actorId, action, status, startDate, endDate, pageable);
            } else {
                // Super admin ou admin : voir tous les logs
                if (organizationId == null) {
                    return ResponseEntity.badRequest().build();
                }
                if (actorId != null) {
                    auditLogs = auditService.getAuditLogsByActorIdAndOrganizationId(actorId, organizationId, pageable);
                } else if (action != null) {
                    auditLogs = auditService.getAuditLogsByActionAndOrganizationId(action, organizationId, pageable);
                } else if (status != null) {
                    auditLogs = auditService.getAuditLogsByStatusAndOrganizationId(status, organizationId, pageable);
                } else if (startDate != null && endDate != null) {
                    auditLogs = auditService.getAuditLogsByDateRange(organizationId, startDate, endDate, pageable);
                } else {
                    auditLogs = auditService.getAuditLogsByOrganizationId(organizationId, pageable);
                }
            }

            Page<AuditLogDTO> auditLogDTOs = auditLogs.map(auditLogMapper::toDTO);
            return ResponseEntity.ok(auditLogDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/target")
    @Operation(summary = "Get audit logs by target", description = "Returns all audit logs for a specific target (e.g., a property, resource)")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogsByTarget(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        List<AuditLog> auditLogs = auditService.getAuditLogsByTarget(targetType, targetId);
        List<AuditLogDTO> auditLogDTOs = auditLogs.stream()
                .map(auditLogMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(auditLogDTOs);
    }
}
