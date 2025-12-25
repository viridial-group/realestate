package com.realestate.user.permission.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.user.permission.entity.Permission;
import com.realestate.user.permission.service.PermissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/organization/{orgId}")
    public List<Permission> getPermissions(@PathVariable UUID orgId) {
        return permissionService.getPermissions(orgId);
    }
}

