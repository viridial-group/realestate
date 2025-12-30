package com.realestate.resource.service;

import com.realestate.resource.entity.Domain;
import com.realestate.resource.entity.Resource;
import com.realestate.resource.entity.ResourceAccess;
import com.realestate.resource.entity.Tag;
import com.realestate.resource.repository.DomainRepository;
import com.realestate.resource.repository.ResourceAccessRepository;
import com.realestate.resource.repository.ResourceRepository;
import com.realestate.resource.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final DomainRepository domainRepository;
    private final ResourceAccessRepository resourceAccessRepository;
    private final TagRepository tagRepository;

    public ResourceService(
            ResourceRepository resourceRepository,
            DomainRepository domainRepository,
            ResourceAccessRepository resourceAccessRepository,
            TagRepository tagRepository) {
        this.resourceRepository = resourceRepository;
        this.domainRepository = domainRepository;
        this.resourceAccessRepository = resourceAccessRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Resource> getResourcesByOrganizationId(Long organizationId) {
        return resourceRepository.findActiveByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public List<Resource> getResourcesByDomainId(Long domainId) {
        return resourceRepository.findByDomainId(domainId);
    }

    @Transactional(readOnly = true)
    public List<Resource> getResourcesByDomainAndOrganization(Long domainId, Long organizationId) {
        return resourceRepository.findByDomainIdAndOrganizationId(domainId, organizationId);
    }

    @Transactional(readOnly = true)
    public List<Resource> getAllSharedResources() {
        return resourceRepository.findAllShared();
    }

    @Transactional
    public Resource updateResource(Long id, Resource resourceDetails) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));

        if (resourceDetails.getName() != null) {
            resource.setName(resourceDetails.getName());
        }
        if (resourceDetails.getDescription() != null) {
            resource.setDescription(resourceDetails.getDescription());
        }
        if (resourceDetails.getActive() != null) {
            resource.setActive(resourceDetails.getActive());
        }
        if (resourceDetails.getShared() != null) {
            resource.setShared(resourceDetails.getShared());
        }
        if (resourceDetails.getMetadata() != null) {
            resource.setMetadata(resourceDetails.getMetadata());
        }

        return resourceRepository.save(resource);
    }

    @Transactional
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new RuntimeException("Resource not found with id: " + id);
        }
        resourceRepository.deleteById(id);
    }

    @Transactional
    public Resource shareResourceWithOrganization(Long resourceId, Long organizationId, Boolean canRead, Boolean canWrite, Boolean canDelete) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        ResourceAccess access = resourceAccessRepository
                .findByResourceIdAndOrganizationId(resourceId, organizationId)
                .orElse(new ResourceAccess(resource, organizationId));

        access.setCanRead(canRead != null ? canRead : true);
        access.setCanWrite(canWrite != null ? canWrite : false);
        access.setCanDelete(canDelete != null ? canDelete : false);
        access.setActive(true);

        resourceAccessRepository.save(access);
        resource.setShared(true);
        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource addTagsToResource(Long resourceId, Set<Long> tagIds) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        java.util.List<Tag> tagList = tagRepository.findAllById(tagIds);
        Set<Tag> tags = new java.util.HashSet<>(tagList);
        resource.getTags().addAll(tags);
        tags.forEach(tag -> tag.getResources().add(resource));

        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource removeTagsFromResource(Long resourceId, Set<Long> tagIds) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        java.util.List<Tag> tagList = tagRepository.findAllById(tagIds);
        Set<Tag> tags = new java.util.HashSet<>(tagList);
        resource.getTags().removeAll(tags);
        tags.forEach(tag -> tag.getResources().remove(resource));

        return resourceRepository.save(resource);
    }
}

