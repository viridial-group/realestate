package com.realestate.property.mapper;

import com.realestate.property.dto.BlogPostDTO;
import com.realestate.property.entity.BlogPost;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre BlogPost entity et BlogPostDTO
 */
@Component
public class BlogPostMapper {

    public BlogPostDTO toDTO(BlogPost blogPost) {
        if (blogPost == null) {
            return null;
        }

        BlogPostDTO dto = new BlogPostDTO();
        dto.setId(blogPost.getId());
        dto.setTitle(blogPost.getTitle());
        dto.setExcerpt(blogPost.getExcerpt());
        dto.setContent(blogPost.getContent());
        dto.setSlug(blogPost.getSlug());
        dto.setCategory(blogPost.getCategory());
        dto.setTags(blogPost.getTags());
        dto.setFeaturedImage(blogPost.getFeaturedImage());
        dto.setMetaDescription(blogPost.getMetaDescription());
        dto.setMetaKeywords(blogPost.getMetaKeywords());
        dto.setOgImage(blogPost.getOgImage());
        dto.setStatus(blogPost.getStatus());
        dto.setPublishedAt(blogPost.getPublishedAt());
        dto.setAuthorId(blogPost.getAuthorId());
        dto.setAuthorName(blogPost.getAuthorName());
        dto.setOrganizationId(blogPost.getOrganizationId());
        dto.setViewCount(blogPost.getViewCount());
        dto.setCreatedAt(blogPost.getCreatedAt());
        dto.setUpdatedAt(blogPost.getUpdatedAt());

        return dto;
    }

    public BlogPost toEntity(BlogPostDTO dto) {
        if (dto == null) {
            return null;
        }

        BlogPost blogPost = new BlogPost();
        blogPost.setId(dto.getId());
        blogPost.setTitle(dto.getTitle());
        blogPost.setExcerpt(dto.getExcerpt());
        blogPost.setContent(dto.getContent());
        blogPost.setSlug(dto.getSlug());
        blogPost.setCategory(dto.getCategory());
        blogPost.setTags(dto.getTags());
        blogPost.setFeaturedImage(dto.getFeaturedImage());
        blogPost.setMetaDescription(dto.getMetaDescription());
        blogPost.setMetaKeywords(dto.getMetaKeywords());
        blogPost.setOgImage(dto.getOgImage());
        blogPost.setStatus(dto.getStatus());
        blogPost.setPublishedAt(dto.getPublishedAt());
        blogPost.setAuthorId(dto.getAuthorId());
        blogPost.setAuthorName(dto.getAuthorName());
        blogPost.setOrganizationId(dto.getOrganizationId());
        blogPost.setViewCount(dto.getViewCount());

        return blogPost;
    }
}

