package com.realestate.identity.mapper;

import com.realestate.identity.dto.RoleDTO;
import com.realestate.identity.entity.Permission;
import com.realestate.identity.entity.Role;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleDTO toDTO(Role role) {
        if (role == null) {
            return null;
        }
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        
        // Mapper les permissions en noms seulement (évite lazy loading)
        if (role.getPermissions() != null) {
            dto.setPermissionNames(role.getPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }

    public Role toEntity(RoleDTO dto) {
        if (dto == null) {
            return null;
        }
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        // Note: permissions doivent être gérées séparément
        return role;
    }
}

