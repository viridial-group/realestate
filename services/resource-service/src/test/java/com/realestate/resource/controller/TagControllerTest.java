package com.realestate.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.resource.entity.Domain;
import com.realestate.resource.entity.Tag;
import com.realestate.resource.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TagController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Autowired
    private ObjectMapper objectMapper;

    private Tag createTestTag() {
        Domain domain = new Domain();
        domain.setId(1L);
        domain.setName("Real Estate");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("luxury");
        tag.setDescription("Luxury properties");
        tag.setDomain(domain);
        tag.setActive(true);
        return tag;
    }

    @Test
    void testCreateTag_Success() throws Exception {
        // Given
        Tag tag = createTestTag();
        when(tagService.createTag(any(Tag.class))).thenReturn(tag);

        // When & Then
        mockMvc.perform(post("/api/resources/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("luxury"));
    }

    @Test
    void testGetTagById_Success() throws Exception {
        // Given
        Long id = 1L;
        Tag tag = createTestTag();
        when(tagService.getTagById(id)).thenReturn(Optional.of(tag));

        // When & Then
        mockMvc.perform(get("/api/resources/tags/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("luxury"));
    }

    @Test
    void testSearchTagsByName_Success() throws Exception {
        // Given
        String name = "luxury";
        List<Tag> tags = Arrays.asList(createTestTag());
        when(tagService.searchTagsByName(name)).thenReturn(tags);

        // When & Then
        mockMvc.perform(get("/api/resources/tags")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetTagsByDomainId_Success() throws Exception {
        // Given
        Long domainId = 1L;
        List<Tag> tags = Arrays.asList(createTestTag());
        when(tagService.getTagsByDomainId(domainId)).thenReturn(tags);

        // When & Then
        mockMvc.perform(get("/api/resources/tags")
                        .param("domainId", domainId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateTag_Success() throws Exception {
        // Given
        Long id = 1L;
        Tag updateDetails = new Tag();
        updateDetails.setName("Updated Tag");
        Tag updated = createTestTag();
        updated.setName("Updated Tag");

        when(tagService.updateTag(eq(id), any(Tag.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/resources/tags/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Tag"));
    }

    @Test
    void testDeleteTag_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(tagService).deleteTag(id);

        // When & Then
        mockMvc.perform(delete("/api/resources/tags/{id}", id))
                .andExpect(status().isNoContent());
    }
}

