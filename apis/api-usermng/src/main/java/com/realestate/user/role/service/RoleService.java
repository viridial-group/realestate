package com.realestate.user.role.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.realestate.user.role.entity.Role;
import com.realestate.user.role.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> getRolesByOrganization(UUID orgId) {
        return roleRepository.findByOrganizationId(orgId);
    }
}

