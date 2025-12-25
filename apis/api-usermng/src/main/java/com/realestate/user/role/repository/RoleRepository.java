package com.realestate.user.role.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.user.role.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findByOrganizationId(UUID organizationId);
}

