package com.realestate.resource.service;

import com.realestate.resource.entity.Domain;
import com.realestate.resource.entity.Tag;
import com.realestate.resource.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag testTag;
    private Domain testDomain;

    @BeforeEach
    void setUp() {
        testDomain = new Domain();
        testDomain.setId(1L);
        testDomain.setName("Real Estate");

        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("luxury");
        testTag.setDescription("Luxury properties");
        testTag.setDomain(testDomain);
        testTag.setActive(true);
    }

    @Test
    void testCreateTag_Success() {
        // Given
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        // When
        Tag result = tagService.createTag(testTag);

        // Then
        assertNotNull(result);
        assertEquals(testTag.getName(), result.getName());
        verify(tagRepository).save(testTag);
    }

    @Test
    void testGetTagById_Success() {
        // Given
        Long id = 1L;
        when(tagRepository.findById(id)).thenReturn(Optional.of(testTag));

        // When
        Optional<Tag> result = tagService.getTagById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTag.getId(), result.get().getId());
        verify(tagRepository).findById(id);
    }

    @Test
    void testSearchTagsByName_Success() {
        // Given
        String name = "luxury";
        List<Tag> tags = Arrays.asList(testTag);
        when(tagRepository.findByNameContainingIgnoreCase(name)).thenReturn(tags);

        // When
        List<Tag> result = tagService.searchTagsByName(name);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tagRepository).findByNameContainingIgnoreCase(name);
    }

    @Test
    void testGetTagsByDomainId_Success() {
        // Given
        Long domainId = 1L;
        List<Tag> tags = Arrays.asList(testTag);
        when(tagRepository.findActiveByDomainId(domainId)).thenReturn(tags);

        // When
        List<Tag> result = tagService.getTagsByDomainId(domainId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tagRepository).findActiveByDomainId(domainId);
    }

    @Test
    void testGetTagsByOrganizationId_Success() {
        // Given
        Long organizationId = 100L;
        List<Tag> tags = Arrays.asList(testTag);
        when(tagRepository.findActiveByOrganizationId(organizationId)).thenReturn(tags);

        // When
        List<Tag> result = tagService.getTagsByOrganizationId(organizationId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tagRepository).findActiveByOrganizationId(organizationId);
    }

    @Test
    void testUpdateTag_Success() {
        // Given
        Long id = 1L;
        Tag updateDetails = new Tag();
        updateDetails.setName("Updated Tag");
        updateDetails.setDescription("Updated Description");

        when(tagRepository.findById(id)).thenReturn(Optional.of(testTag));
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        // When
        Tag result = tagService.updateTag(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Tag", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(tagRepository).findById(id);
        verify(tagRepository).save(testTag);
    }

    @Test
    void testUpdateTag_NotFound() {
        // Given
        Long id = 999L;
        Tag updateDetails = new Tag();
        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> tagService.updateTag(id, updateDetails));
        verify(tagRepository).findById(id);
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void testDeleteTag_Success() {
        // Given
        Long id = 1L;
        when(tagRepository.existsById(id)).thenReturn(true);
        doNothing().when(tagRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> tagService.deleteTag(id));

        // Then
        verify(tagRepository).existsById(id);
        verify(tagRepository).deleteById(id);
    }

    @Test
    void testDeleteTag_NotFound() {
        // Given
        Long id = 999L;
        when(tagRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> tagService.deleteTag(id));
        verify(tagRepository).existsById(id);
        verify(tagRepository, never()).deleteById(anyLong());
    }
}

