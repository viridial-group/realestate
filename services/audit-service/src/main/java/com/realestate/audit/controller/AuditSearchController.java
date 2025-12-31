package com.realestate.audit.controller;

import com.realestate.common.document.AuditLogDocument;
import com.realestate.audit.service.AuditSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller pour la recherche Elasticsearch des Audit Logs
 */
@RestController
@RequestMapping("/api/audit/search")
@Tag(name = "Audit Search", description = "Elasticsearch search API for audit logs")
public class AuditSearchController {

    private final AuditSearchService auditSearchService;

    public AuditSearchController(AuditSearchService auditSearchService) {
        this.auditSearchService = auditSearchService;
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Search by organization", description = "Search audit logs by organization ID")
    public ResponseEntity<Page<AuditLogDocument>> searchByOrganization(
            @PathVariable Long organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDocument> results = auditSearchService.searchByOrganization(organizationId, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/actor/{actorId}")
    @Operation(summary = "Search by actor", description = "Search audit logs by actor (user) ID")
    public ResponseEntity<Page<AuditLogDocument>> searchByActor(
            @PathVariable Long actorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDocument> results = auditSearchService.searchByActor(actorId, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/action/{action}")
    @Operation(summary = "Search by action", description = "Search audit logs by action type")
    public ResponseEntity<List<AuditLogDocument>> searchByAction(@PathVariable String action) {
        List<AuditLogDocument> results = auditSearchService.searchByAction(action);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/target-type/{targetType}")
    @Operation(summary = "Search by target type", description = "Search audit logs by target resource type")
    public ResponseEntity<List<AuditLogDocument>> searchByTargetType(@PathVariable String targetType) {
        List<AuditLogDocument> results = auditSearchService.searchByTargetType(targetType);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/organization/{organizationId}/action/{action}")
    @Operation(summary = "Search by organization and action", description = "Search audit logs by organization and action")
    public ResponseEntity<Page<AuditLogDocument>> searchByOrganizationAndAction(
            @PathVariable Long organizationId,
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDocument> results = auditSearchService.searchByOrganizationAndAction(
                organizationId, action, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/organization/{organizationId}/target-type/{targetType}")
    @Operation(summary = "Search by organization and target type", description = "Search audit logs by organization and target type")
    public ResponseEntity<Page<AuditLogDocument>> searchByOrganizationAndTargetType(
            @PathVariable Long organizationId,
            @PathVariable String targetType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDocument> results = auditSearchService.searchByOrganizationAndTargetType(
                organizationId, targetType, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Search by date range", description = "Search audit logs by date range")
    public ResponseEntity<Page<AuditLogDocument>> searchByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDocument> results = auditSearchService.searchByDateRange(start, end, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/organization/{organizationId}/date-range")
    @Operation(summary = "Search by organization and date range", description = "Search audit logs by organization and date range")
    public ResponseEntity<Page<AuditLogDocument>> searchByOrganizationAndDateRange(
            @PathVariable Long organizationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDocument> results = auditSearchService.searchByOrganizationAndDateRange(
                organizationId, start, end, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/text")
    @Operation(summary = "Full-text search", description = "Search audit logs by text in description")
    public ResponseEntity<Page<AuditLogDocument>> searchByText(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDocument> results = auditSearchService.searchByText(q, pageable);
        return ResponseEntity.ok(results);
    }
}

