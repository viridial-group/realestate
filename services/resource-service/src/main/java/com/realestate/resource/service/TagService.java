package com.realestate.resource.service;

import com.realestate.resource.entity.Tag;
import com.realestate.resource.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional(readOnly = true)
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Tag> searchTagsByName(String name) {
        return tagRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Tag> getTagsByDomainId(Long domainId) {
        return tagRepository.findActiveByDomainId(domainId);
    }

    @Transactional(readOnly = true)
    public List<Tag> getTagsByOrganizationId(Long organizationId) {
        return tagRepository.findActiveByOrganizationId(organizationId);
    }

    @Transactional
    public Tag updateTag(Long id, Tag tagDetails) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));

        if (tagDetails.getName() != null) {
            tag.setName(tagDetails.getName());
        }
        if (tagDetails.getDescription() != null) {
            tag.setDescription(tagDetails.getDescription());
        }
        if (tagDetails.getActive() != null) {
            tag.setActive(tagDetails.getActive());
        }

        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
}

