package com.realestate.identity.mapper;

import com.realestate.identity.dto.UserDTO;
import com.realestate.identity.entity.Role;
import com.realestate.identity.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.getEnabled());
        dto.setAccountNonExpired(user.getAccountNonExpired());
        dto.setAccountNonLocked(user.getAccountNonLocked());
        dto.setCredentialsNonExpired(user.getCredentialsNonExpired());
        
        // Mapper les rôles en noms seulement (évite lazy loading)
        if (user.getRoles() != null) {
            dto.setRoleNames(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }
        
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEnabled(dto.getEnabled());
        user.setAccountNonExpired(dto.getAccountNonExpired());
        user.setAccountNonLocked(dto.getAccountNonLocked());
        user.setCredentialsNonExpired(dto.getCredentialsNonExpired());
        // Note: password et roles doivent être gérés séparément
        return user;
    }
}

