package com.realestate.audit.controller;

import com.realestate.audit.entity.AuditLog;
import com.realestate.audit.service.AuditService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit Logs", description = "Audit and logging API for complete traceability of all critical actions")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping
    @Operation(summary = "Create audit log", description = "Creates a new audit log entry")
    public ResponseEntity<AuditLog> createAuditLog(@Valid @RequestBody AuditLog auditLog) {
        AuditLog created = auditService.createAuditLog(auditLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/log")
    @Operation(summary = "Log action", description = "Logs an action with all relevant details")
    public ResponseEntity<AuditLog> logAction(@RequestBody Map<String, Object> request) {
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
            return ResponseEntity.status(HttpStatus.CREATED).body(logged);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audit log by ID", description = "Returns audit log information for a specific audit log ID")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        return auditService.getAuditLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List audit logs", description = "Returns a paginated list of audit logs with various filters")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam Long organizationId,
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogs;

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

        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/target")
    @Operation(summary = "Get audit logs by target", description = "Returns all audit logs for a specific target (e.g., a property, resource)")
    public ResponseEntity<List<AuditLog>> getAuditLogsByTarget(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        List<AuditLog> auditLogs = auditService.getAuditLogsByTarget(targetType, targetId);
        return ResponseEntity.ok(auditLogs);
    }
}

