package com.realestate.identity.config;

import com.realestate.identity.entity.Permission;
import com.realestate.identity.entity.Role;
import com.realestate.identity.entity.User;
import com.realestate.identity.repository.PermissionRepository;
import com.realestate.identity.repository.RoleRepository;
import com.realestate.identity.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @Transactional
    public CommandLineRunner initializeData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Initialize only if no roles exist
            if (roleRepository.count() > 0) {
                logger.info("Roles and permissions already initialized. Skipping...");
                return;
            }

            logger.info("Initializing default roles and permissions...");

            // Create Permissions
            Permission userRead = createPermissionIfNotExists(permissionRepository,
                    "USER_READ", "user", "READ", "Read user information");
            Permission userWrite = createPermissionIfNotExists(permissionRepository,
                    "USER_WRITE", "user", "WRITE", "Create and update users");
            Permission userDelete = createPermissionIfNotExists(permissionRepository,
                    "USER_DELETE", "user", "DELETE", "Delete users");
            Permission organizationRead = createPermissionIfNotExists(permissionRepository,
                    "ORGANIZATION_READ", "organization", "READ", "Read organization information");
            Permission organizationWrite = createPermissionIfNotExists(permissionRepository,
                    "ORGANIZATION_WRITE", "organization", "WRITE", "Create and update organizations");
            Permission propertyRead = createPermissionIfNotExists(permissionRepository,
                    "PROPERTY_READ", "property", "READ", "Read property information");
            Permission propertyWrite = createPermissionIfNotExists(permissionRepository,
                    "PROPERTY_WRITE", "property", "WRITE", "Create and update properties");

            // Create Roles
            Role adminRole = createRoleIfNotExists(roleRepository, "ADMIN", "Administrator with full access");
            Role userRole = createRoleIfNotExists(roleRepository, "USER", "Standard user with basic access");
            Role managerRole = createRoleIfNotExists(roleRepository, "MANAGER", "Manager with elevated access");

            // Assign permissions to roles
            // ADMIN: All permissions
            adminRole.getPermissions().addAll(Set.of(
                    userRead, userWrite, userDelete,
                    organizationRead, organizationWrite,
                    propertyRead, propertyWrite
            ));

            // MANAGER: Read and write permissions (no delete)
            managerRole.getPermissions().addAll(Set.of(
                    userRead, userWrite,
                    organizationRead, organizationWrite,
                    propertyRead, propertyWrite
            ));

            // USER: Read permissions only
            userRole.getPermissions().addAll(Set.of(
                    userRead,
                    organizationRead,
                    propertyRead
            ));

            roleRepository.saveAll(Set.of(adminRole, managerRole, userRole));

            // Create default admin user if not exists
            if (!userRepository.findByEmail("admin@viridial.com").isPresent()) {
                User adminUser = new User();
                adminUser.setEmail("admin@viridial.com");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setFirstName("Admin");
                adminUser.setLastName("User");
                adminUser.setEnabled(true);
                adminUser.setAccountNonExpired(true);
                adminUser.setAccountNonLocked(true);
                adminUser.setCredentialsNonExpired(true);
                adminUser.setRoles(Set.of(adminRole));
                userRepository.save(adminUser);
                logger.info("Default admin user created: admin@viridial.com / admin123");
            }

            logger.info("Roles and permissions initialization completed!");
        };
    }

    private Permission createPermissionIfNotExists(
            PermissionRepository repository,
            String name, String resource, String action, String description) {
        return repository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission(name, resource, action, description);
                    Permission saved = repository.save(permission);
                    logger.info("Created permission: {} ({})", name, resource + ":" + action);
                    return saved;
                });
    }

    private Role createRoleIfNotExists(RoleRepository repository, String name, String description) {
        return repository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role(name, description);
                    Role saved = repository.save(role);
                    logger.info("Created role: {}", name);
                    return saved;
                });
    }
}

