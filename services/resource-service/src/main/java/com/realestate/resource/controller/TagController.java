package com.realestate.resource.controller;

import com.realestate.resource.entity.Tag;
import com.realestate.resource.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Resource Tags", description = "Tag management API for organizing and categorizing resources")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    @Operation(summary = "Create tag", description = "Creates a new tag for categorizing resources")
    public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag) {
        Tag created = tagService.createTag(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tag by ID", description = "Returns tag information for a specific tag ID")
    public ResponseEntity<Tag> getTagById(@PathVariable Long id) {
        return tagService.getTagById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Search tags", description = "Searches tags by name, domain, or organization")
    public ResponseEntity<List<Tag>> searchTags(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long domainId,
            @RequestParam(required = false) Long organizationId) {
        List<Tag> tags;
        
        if (name != null) {
            tags = tagService.searchTagsByName(name);
        } else if (domainId != null) {
            tags = tagService.getTagsByDomainId(domainId);
        } else if (organizationId != null) {
            tags = tagService.getTagsByOrganizationId(organizationId);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(tags);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tag", description = "Updates tag information for a specific tag ID")
    public ResponseEntity<Tag> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody Tag tagDetails) {
        try {
            Tag updated = tagService.updateTag(id, tagDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tag", description = "Deletes a tag from the database by ID")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        try {
            tagService.deleteTag(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

