package com.realestate.document.mapper;

import com.realestate.document.dto.DocumentDTO;
import com.realestate.document.entity.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentDTO toDTO(Document document) {
        if (document == null) {
            return null;
        }
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setName(document.getName());
        dto.setDescription(document.getDescription());
        dto.setType(document.getType());
        dto.setMimeType(document.getMimeType());
        dto.setFileSize(document.getFileSize());
        dto.setFilePath(document.getFilePath());
        dto.setUrl(document.getUrl());
        dto.setResourceId(document.getResourceId());
        dto.setPropertyId(document.getPropertyId());
        dto.setOrganizationId(document.getOrganizationId());
        dto.setCreatedBy(document.getCreatedBy());
        dto.setActive(document.getActive());
        dto.setMetadata(document.getMetadata());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        return dto;
    }

    public Document toEntity(DocumentDTO dto) {
        if (dto == null) {
            return null;
        }
        Document document = new Document();
        document.setId(dto.getId());
        document.setName(dto.getName());
        document.setDescription(dto.getDescription());
        document.setType(dto.getType());
        document.setMimeType(dto.getMimeType());
        document.setFileSize(dto.getFileSize());
        document.setFilePath(dto.getFilePath());
        document.setUrl(dto.getUrl());
        document.setResourceId(dto.getResourceId());
        document.setPropertyId(dto.getPropertyId());
        document.setOrganizationId(dto.getOrganizationId());
        document.setCreatedBy(dto.getCreatedBy());
        document.setActive(dto.getActive());
        document.setMetadata(dto.getMetadata());
        return document;
    }
}

