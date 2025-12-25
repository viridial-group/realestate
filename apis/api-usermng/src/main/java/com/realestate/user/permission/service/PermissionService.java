package com.realestate.user.permission.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.realestate.user.permission.entity.Permission;
import com.realestate.user.permission.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<Permission> getPermissions(UUID orgId) {
        return permissionRepository.findByOrganizationId(orgId);
    }
}

