package com.realestate.resource.mapper;

import com.realestate.resource.dto.TagDTO;
import com.realestate.resource.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagDTO toDTO(Tag tag) {
        if (tag == null) {
            return null;
        }
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setDescription(tag.getDescription());
        dto.setOrganizationId(tag.getOrganizationId());
        dto.setActive(tag.getActive());
        dto.setCreatedAt(tag.getCreatedAt());
        dto.setUpdatedAt(tag.getUpdatedAt());
        return dto;
    }

    public Tag toEntity(TagDTO dto) {
        if (dto == null) {
            return null;
        }
        Tag tag = new Tag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        tag.setDescription(dto.getDescription());
        tag.setOrganizationId(dto.getOrganizationId());
        tag.setActive(dto.getActive());
        return tag;
    }
}

