package com.realestate.user.role.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.user.role.entity.Role;
import com.realestate.user.role.service.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/organization/{orgId}")
    public List<Role> getRoles(@PathVariable UUID orgId) {
        return roleService.getRolesByOrganization(orgId);
    }
}

