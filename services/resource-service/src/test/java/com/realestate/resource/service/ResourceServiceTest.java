package com.realestate.resource.service;

import com.realestate.resource.entity.Domain;
import com.realestate.resource.entity.Resource;
import com.realestate.resource.entity.Tag;
import com.realestate.resource.repository.DomainRepository;
import com.realestate.resource.repository.ResourceAccessRepository;
import com.realestate.resource.repository.ResourceRepository;
import com.realestate.resource.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private DomainRepository domainRepository;

    @Mock
    private ResourceAccessRepository resourceAccessRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Domain testDomain;
    private Resource testResource;

    @BeforeEach
    void setUp() {
        testDomain = new Domain();
        testDomain.setId(1L);
        testDomain.setName("Real Estate");

        testResource = new Resource();
        testResource.setId(1L);
        testResource.setName("Test Resource");
        testResource.setDescription("Test Description");
        testResource.setDomain(testDomain);
        testResource.setOrganizationId(100L);
        testResource.setCreatedBy(1L);
        testResource.setActive(true);
        testResource.setShared(false);
    }

    @Test
    void testCreateResource_Success() {
        // Given
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);

        // When
        Resource result = resourceService.createResource(testResource);

        // Then
        assertNotNull(result);
        assertEquals(testResource.getName(), result.getName());
        verify(resourceRepository).save(testResource);
    }

    @Test
    void testGetResourceById_Success() {
        // Given
        Long id = 1L;
        when(resourceRepository.findById(id)).thenReturn(Optional.of(testResource));

        // When
        Optional<Resource> result = resourceService.getResourceById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testResource.getId(), result.get().getId());
        verify(resourceRepository).findById(id);
    }

    @Test
    void testGetResourcesByOrganizationId_Success() {
        // Given
        Long organizationId = 100L;
        List<Resource> resources = Arrays.asList(testResource);
        when(resourceRepository.findActiveByOrganizationId(organizationId)).thenReturn(resources);

        // When
        List<Resource> result = resourceService.getResourcesByOrganizationId(organizationId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(resourceRepository).findActiveByOrganizationId(organizationId);
    }

    @Test
    void testGetResourcesByDomainId_Success() {
        // Given
        Long domainId = 1L;
        List<Resource> resources = Arrays.asList(testResource);
        when(resourceRepository.findByDomainId(domainId)).thenReturn(resources);

        // When
        List<Resource> result = resourceService.getResourcesByDomainId(domainId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(resourceRepository).findByDomainId(domainId);
    }

    @Test
    void testGetAllSharedResources_Success() {
        // Given
        testResource.setShared(true);
        List<Resource> sharedResources = Arrays.asList(testResource);
        when(resourceRepository.findAllShared()).thenReturn(sharedResources);

        // When
        List<Resource> result = resourceService.getAllSharedResources();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getShared());
        verify(resourceRepository).findAllShared();
    }

    @Test
    void testUpdateResource_Success() {
        // Given
        Long id = 1L;
        Resource updateDetails = new Resource();
        updateDetails.setName("Updated Resource");
        updateDetails.setDescription("Updated Description");
        updateDetails.setShared(true);

        when(resourceRepository.findById(id)).thenReturn(Optional.of(testResource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);

        // When
        Resource result = resourceService.updateResource(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Resource", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertTrue(result.getShared());
        verify(resourceRepository).findById(id);
        verify(resourceRepository).save(testResource);
    }

    @Test
    void testUpdateResource_NotFound() {
        // Given
        Long id = 999L;
        Resource updateDetails = new Resource();
        when(resourceRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> resourceService.updateResource(id, updateDetails));
        verify(resourceRepository).findById(id);
        verify(resourceRepository, never()).save(any(Resource.class));
    }

    @Test
    void testDeleteResource_Success() {
        // Given
        Long id = 1L;
        when(resourceRepository.existsById(id)).thenReturn(true);
        doNothing().when(resourceRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> resourceService.deleteResource(id));

        // Then
        verify(resourceRepository).existsById(id);
        verify(resourceRepository).deleteById(id);
    }

    @Test
    void testShareResourceWithOrganization_Success() {
        // Given
        Long resourceId = 1L;
        Long organizationId = 200L;
        Boolean canRead = true;
        Boolean canWrite = true;
        Boolean canDelete = false;

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(testResource));
        when(resourceAccessRepository.findByResourceIdAndOrganizationId(resourceId, organizationId))
                .thenReturn(Optional.empty());
        when(resourceAccessRepository.save(any())).thenReturn(null);
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);

        // When
        Resource result = resourceService.shareResourceWithOrganization(resourceId, organizationId, canRead, canWrite, canDelete);

        // Then
        assertNotNull(result);
        assertTrue(result.getShared());
        verify(resourceRepository).findById(resourceId);
        verify(resourceAccessRepository).save(any());
        verify(resourceRepository).save(testResource);
    }

    @Test
    void testAddTagsToResource_Success() {
        // Given
        Long resourceId = 1L;
        Set<Long> tagIds = Set.of(1L, 2L);
        Tag tag1 = new Tag("Tag1");
        Tag tag2 = new Tag("Tag2");
        List<Tag> tags = Arrays.asList(tag1, tag2);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(testResource));
        when(tagRepository.findAllById(tagIds)).thenReturn(tags);
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);

        // When
        Resource result = resourceService.addTagsToResource(resourceId, tagIds);

        // Then
        assertNotNull(result);
        verify(resourceRepository).findById(resourceId);
        verify(tagRepository).findAllById(tagIds);
        verify(resourceRepository).save(testResource);
    }

    @Test
    void testRemoveTagsFromResource_Success() {
        // Given
        Long resourceId = 1L;
        Set<Long> tagIds = Set.of(1L);
        Tag tag = new Tag("Tag1");
        List<Tag> tags = Arrays.asList(tag);
        testResource.getTags().add(tag);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(testResource));
        when(tagRepository.findAllById(tagIds)).thenReturn(tags);
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);

        // When
        Resource result = resourceService.removeTagsFromResource(resourceId, tagIds);

        // Then
        assertNotNull(result);
        verify(resourceRepository).findById(resourceId);
        verify(tagRepository).findAllById(tagIds);
        verify(resourceRepository).save(testResource);
    }
}

