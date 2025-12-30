package com.realestate.resource.controller;

import com.realestate.resource.entity.Resource;
import com.realestate.resource.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Resources", description = "Generic resource management API for managing resources across different domains")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    @Operation(summary = "Create resource", description = "Creates a new resource in a specific domain")
    public ResponseEntity<Resource> createResource(@Valid @RequestBody Resource resource) {
        Resource created = resourceService.createResource(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID", description = "Returns resource information for a specific resource ID")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List resources", description = "Returns a list of resources filtered by organization, domain, or shared status")
    public ResponseEntity<List<Resource>> getResources(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Long domainId,
            @RequestParam(required = false) Boolean shared) {
        List<Resource> resources;
        
        if (shared != null && shared) {
            resources = resourceService.getAllSharedResources();
        } else if (domainId != null && organizationId != null) {
            resources = resourceService.getResourcesByDomainAndOrganization(domainId, organizationId);
        } else if (organizationId != null) {
            resources = resourceService.getResourcesByOrganizationId(organizationId);
        } else if (domainId != null) {
            resources = resourceService.getResourcesByDomainId(domainId);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update resource", description = "Updates resource information for a specific resource ID")
    public ResponseEntity<Resource> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody Resource resourceDetails) {
        try {
            Resource updated = resourceService.updateResource(id, resourceDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resource", description = "Deletes a resource from the database by ID")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        try {
            resourceService.deleteResource(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/share")
    @Operation(summary = "Share resource with organization", description = "Shares a resource with another organization with specific permissions")
    public ResponseEntity<Resource> shareResource(
            @PathVariable Long id,
            @RequestBody Map<String, Object> shareRequest) {
        try {
            Long organizationId = Long.valueOf(shareRequest.get("organizationId").toString());
            Boolean canRead = shareRequest.containsKey("canRead") ? (Boolean) shareRequest.get("canRead") : true;
            Boolean canWrite = shareRequest.containsKey("canWrite") ? (Boolean) shareRequest.get("canWrite") : false;
            Boolean canDelete = shareRequest.containsKey("canDelete") ? (Boolean) shareRequest.get("canDelete") : false;
            
            Resource shared = resourceService.shareResourceWithOrganization(id, organizationId, canRead, canWrite, canDelete);
            return ResponseEntity.ok(shared);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/tags")
    @Operation(summary = "Add tags to resource", description = "Adds one or more tags to a resource")
    public ResponseEntity<Resource> addTagsToResource(
            @PathVariable Long id,
            @RequestBody Map<String, Set<Long>> request) {
        try {
            Set<Long> tagIds = request.get("tagIds");
            Resource updated = resourceService.addTagsToResource(id, tagIds);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/tags")
    @Operation(summary = "Remove tags from resource", description = "Removes one or more tags from a resource")
    public ResponseEntity<Resource> removeTagsFromResource(
            @PathVariable Long id,
            @RequestBody Map<String, Set<Long>> request) {
        try {
            Set<Long> tagIds = request.get("tagIds");
            Resource updated = resourceService.removeTagsFromResource(id, tagIds);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

