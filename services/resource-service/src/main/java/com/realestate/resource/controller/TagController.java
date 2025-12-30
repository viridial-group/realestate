package com.realestate.resource.controller;

import com.realestate.resource.dto.TagDTO;
import com.realestate.resource.entity.Tag;
import com.realestate.resource.mapper.TagMapper;
import com.realestate.resource.service.TagService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Resource Tags", description = "Tag management API for organizing and categorizing resources")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    public TagController(TagService tagService, TagMapper tagMapper) {
        this.tagService = tagService;
        this.tagMapper = tagMapper;
    }

    @PostMapping
    @Operation(summary = "Create tag", description = "Creates a new tag for categorizing resources")
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody TagDTO tagDTO) {
        Tag tag = tagMapper.toEntity(tagDTO);
        Tag created = tagService.createTag(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(tagMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tag by ID", description = "Returns tag information for a specific tag ID")
    public ResponseEntity<TagDTO> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
        return ResponseEntity.ok(tagMapper.toDTO(tag));
    }

    @GetMapping
    @Operation(summary = "Search tags", description = "Searches tags by name, domain, or organization")
    public ResponseEntity<List<TagDTO>> searchTags(
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
        
        List<TagDTO> tagDTOs = tags.stream()
                .map(tagMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tagDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tag", description = "Updates tag information for a specific tag ID")
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagDTO tagDTO) {
        Tag tag = tagMapper.toEntity(tagDTO);
        Tag updated = tagService.updateTag(id, tag);
        return ResponseEntity.ok(tagMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tag", description = "Deletes a tag from the database by ID")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
