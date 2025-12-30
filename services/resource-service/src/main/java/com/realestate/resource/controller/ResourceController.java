package com.realestate.resource.controller;

import com.realestate.resource.dto.ResourceDTO;
import com.realestate.resource.entity.Resource;
import com.realestate.resource.mapper.ResourceMapper;
import com.realestate.resource.service.ResourceService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Resources", description = "Generic resource management API for managing resources across different domains")
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    public ResourceController(ResourceService resourceService, ResourceMapper resourceMapper) {
        this.resourceService = resourceService;
        this.resourceMapper = resourceMapper;
    }

    @PostMapping
    @Operation(summary = "Create resource", description = "Creates a new resource in a specific domain")
    public ResponseEntity<ResourceDTO> createResource(@Valid @RequestBody ResourceDTO resourceDTO) {
        Resource resource = resourceMapper.toEntity(resourceDTO);
        Resource created = resourceService.createResource(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID", description = "Returns resource information for a specific resource ID")
    public ResponseEntity<ResourceDTO> getResourceById(@PathVariable Long id) {
        Resource resource = resourceService.getResourceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));
        return ResponseEntity.ok(resourceMapper.toDTO(resource));
    }

    @GetMapping
    @Operation(summary = "List resources", description = "Returns a list of resources filtered by organization, domain, or shared status")
    public ResponseEntity<List<ResourceDTO>> getResources(
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
        
        List<ResourceDTO> resourceDTOs = resources.stream()
                .map(resourceMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resourceDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update resource", description = "Updates resource information for a specific resource ID")
    public ResponseEntity<ResourceDTO> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceDTO resourceDTO) {
        Resource resource = resourceMapper.toEntity(resourceDTO);
        Resource updated = resourceService.updateResource(id, resource);
        return ResponseEntity.ok(resourceMapper.toDTO(updated));
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
    public ResponseEntity<ResourceDTO> shareResource(
            @PathVariable Long id,
            @RequestBody Map<String, Object> shareRequest) {
        Long organizationId = Long.valueOf(shareRequest.get("organizationId").toString());
        Boolean canRead = shareRequest.containsKey("canRead") ? (Boolean) shareRequest.get("canRead") : true;
        Boolean canWrite = shareRequest.containsKey("canWrite") ? (Boolean) shareRequest.get("canWrite") : false;
        Boolean canDelete = shareRequest.containsKey("canDelete") ? (Boolean) shareRequest.get("canDelete") : false;
        
        Resource shared = resourceService.shareResourceWithOrganization(id, organizationId, canRead, canWrite, canDelete);
        return ResponseEntity.ok(resourceMapper.toDTO(shared));
    }

    @PostMapping("/{id}/tags")
    @Operation(summary = "Add tags to resource", description = "Adds one or more tags to a resource")
    public ResponseEntity<ResourceDTO> addTagsToResource(
            @PathVariable Long id,
            @RequestBody Map<String, Set<Long>> request) {
        Set<Long> tagIds = request.get("tagIds");
        Resource updated = resourceService.addTagsToResource(id, tagIds);
        return ResponseEntity.ok(resourceMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}/tags")
    @Operation(summary = "Remove tags from resource", description = "Removes one or more tags from a resource")
    public ResponseEntity<ResourceDTO> removeTagsFromResource(
            @PathVariable Long id,
            @RequestBody Map<String, Set<Long>> request) {
        Set<Long> tagIds = request.get("tagIds");
        Resource updated = resourceService.removeTagsFromResource(id, tagIds);
        return ResponseEntity.ok(resourceMapper.toDTO(updated));
    }
}

