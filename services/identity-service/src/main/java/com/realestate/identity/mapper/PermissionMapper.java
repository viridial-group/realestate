package com.realestate.identity.mapper;

import com.realestate.identity.dto.PermissionDTO;
import com.realestate.identity.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionDTO toDTO(Permission permission) {
        if (permission == null) {
            return null;
        }
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setResource(permission.getResource());
        dto.setAction(permission.getAction());
        dto.setDescription(permission.getDescription());
        return dto;
    }

    public Permission toEntity(PermissionDTO dto) {
        if (dto == null) {
            return null;
        }
        Permission permission = new Permission();
        permission.setId(dto.getId());
        permission.setName(dto.getName());
        permission.setResource(dto.getResource());
        permission.setAction(dto.getAction());
        permission.setDescription(dto.getDescription());
        return permission;
    }
}

