package com.realestate.property.controller;

import com.realestate.property.dto.BlogPostDTO;
import com.realestate.property.entity.BlogPost;
import com.realestate.property.mapper.BlogPostMapper;
import com.realestate.property.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur public pour les articles de blog
 * Optimisé pour le SEO
 */
@RestController
@RequestMapping("/api/public/blog")
@Tag(name = "Public Blog", description = "Public blog posts API for SEO")
public class PublicBlogController {

    private final BlogPostService blogPostService;
    private final BlogPostMapper blogPostMapper;

    public PublicBlogController(BlogPostService blogPostService, BlogPostMapper blogPostMapper) {
        this.blogPostService = blogPostService;
        this.blogPostMapper = blogPostMapper;
    }

    @GetMapping
    @Operation(summary = "Get published blog posts", description = "Returns paginated list of published blog posts")
    public ResponseEntity<Page<BlogPostDTO>> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BlogPost> posts = blogPostService.getPublishedPosts(pageable);
        Page<BlogPostDTO> dtos = posts.map(blogPostMapper::toDTO);
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent blog posts", description = "Returns recent published blog posts")
    public ResponseEntity<List<BlogPostDTO>> getRecentPosts(
            @RequestParam(defaultValue = "5") int limit) {
        
        List<BlogPost> posts = blogPostService.getRecentPublishedPosts(limit);
        List<BlogPostDTO> dtos = posts.stream()
                .map(blogPostMapper::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get blog post by slug", description = "Returns a published blog post by its SEO-friendly slug")
    public ResponseEntity<BlogPostDTO> getPostBySlug(@PathVariable String slug) {
        return blogPostService.getPublishedPostBySlug(slug)
                .map(blogPostMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get blog posts by category", description = "Returns paginated list of published blog posts by category")
    public ResponseEntity<Page<BlogPostDTO>> getPostsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<BlogPost> posts = blogPostService.getPublishedPostsByCategory(category, pageable);
        Page<BlogPostDTO> dtos = posts.map(blogPostMapper::toDTO);
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Get blog posts by tag", description = "Returns paginated list of published blog posts by tag")
    public ResponseEntity<Page<BlogPostDTO>> getPostsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<BlogPost> posts = blogPostService.getPublishedPostsByTag(tag, pageable);
        Page<BlogPostDTO> dtos = posts.map(blogPostMapper::toDTO);
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    @Operation(summary = "Search blog posts", description = "Searches in published blog posts")
    public ResponseEntity<Page<BlogPostDTO>> searchPosts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<BlogPost> posts = blogPostService.searchPublishedPosts(q, pageable);
        Page<BlogPostDTO> dtos = posts.map(blogPostMapper::toDTO);
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Returns list of all distinct categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(blogPostService.getCategories());
    }
}

