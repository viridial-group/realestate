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

            // Create Permissions - User
            Permission userRead = createPermissionIfNotExists(permissionRepository,
                    "USER_READ", "user", "READ", "Read user information");
            Permission userWrite = createPermissionIfNotExists(permissionRepository,
                    "USER_WRITE", "user", "WRITE", "Create and update users");
            Permission userDelete = createPermissionIfNotExists(permissionRepository,
                    "USER_DELETE", "user", "DELETE", "Delete users");
            
            // Create Permissions - Organization
            Permission organizationRead = createPermissionIfNotExists(permissionRepository,
                    "ORGANIZATION_READ", "organization", "READ", "Read organization information");
            Permission organizationWrite = createPermissionIfNotExists(permissionRepository,
                    "ORGANIZATION_WRITE", "organization", "WRITE", "Create and update organizations");
            Permission organizationDelete = createPermissionIfNotExists(permissionRepository,
                    "ORGANIZATION_DELETE", "organization", "DELETE", "Delete organizations");
            
            // Create Permissions - Property
            Permission propertyRead = createPermissionIfNotExists(permissionRepository,
                    "PROPERTY_READ", "property", "READ", "Read property information");
            Permission propertyWrite = createPermissionIfNotExists(permissionRepository,
                    "PROPERTY_WRITE", "property", "WRITE", "Create and update properties");
            Permission propertyDelete = createPermissionIfNotExists(permissionRepository,
                    "PROPERTY_DELETE", "property", "DELETE", "Delete properties");
            
            // Create Permissions - Document
            Permission documentRead = createPermissionIfNotExists(permissionRepository,
                    "DOCUMENT_READ", "document", "READ", "Read document information");
            Permission documentWrite = createPermissionIfNotExists(permissionRepository,
                    "DOCUMENT_WRITE", "document", "WRITE", "Create and update documents");
            Permission documentDelete = createPermissionIfNotExists(permissionRepository,
                    "DOCUMENT_DELETE", "document", "DELETE", "Delete documents");
            
            // Create Permissions - Workflow
            Permission workflowRead = createPermissionIfNotExists(permissionRepository,
                    "WORKFLOW_READ", "workflow", "READ", "Read workflow information");
            Permission workflowWrite = createPermissionIfNotExists(permissionRepository,
                    "WORKFLOW_WRITE", "workflow", "WRITE", "Create and update workflows");
            Permission workflowDelete = createPermissionIfNotExists(permissionRepository,
                    "WORKFLOW_DELETE", "workflow", "DELETE", "Delete workflows");
            Permission workflowApprove = createPermissionIfNotExists(permissionRepository,
                    "WORKFLOW_APPROVE", "workflow", "APPROVE", "Approve workflows");
            
            // Create Permissions - Billing
            Permission billingRead = createPermissionIfNotExists(permissionRepository,
                    "BILLING_READ", "billing", "READ", "Read billing information");
            Permission billingWrite = createPermissionIfNotExists(permissionRepository,
                    "BILLING_WRITE", "billing", "WRITE", "Create and update billing");
            Permission billingManage = createPermissionIfNotExists(permissionRepository,
                    "BILLING_MANAGE", "billing", "MANAGE", "Manage billing and plans");
            
            // Create Permissions - Audit
            Permission auditRead = createPermissionIfNotExists(permissionRepository,
                    "AUDIT_READ", "audit", "READ", "Read audit logs");
            Permission auditManage = createPermissionIfNotExists(permissionRepository,
                    "AUDIT_MANAGE", "audit", "MANAGE", "Manage audit logs");
            
            // Create Permissions - Notification
            Permission notificationRead = createPermissionIfNotExists(permissionRepository,
                    "NOTIFICATION_READ", "notification", "READ", "Read notifications");
            Permission notificationWrite = createPermissionIfNotExists(permissionRepository,
                    "NOTIFICATION_WRITE", "notification", "WRITE", "Create and send notifications");
            
            // Create Permissions - Contact Messages
            Permission contactRead = createPermissionIfNotExists(permissionRepository,
                    "CONTACT_READ", "contact", "READ", "Read contact messages");
            Permission contactWrite = createPermissionIfNotExists(permissionRepository,
                    "CONTACT_WRITE", "contact", "WRITE", "Create and update contact messages");
            Permission contactDelete = createPermissionIfNotExists(permissionRepository,
                    "CONTACT_DELETE", "contact", "DELETE", "Delete contact messages");
            
            // Create Permissions - Role & Permission Management
            Permission roleRead = createPermissionIfNotExists(permissionRepository,
                    "ROLE_READ", "role", "READ", "Read roles and permissions");
            Permission roleWrite = createPermissionIfNotExists(permissionRepository,
                    "ROLE_WRITE", "role", "WRITE", "Create and update roles");
            Permission roleDelete = createPermissionIfNotExists(permissionRepository,
                    "ROLE_DELETE", "role", "DELETE", "Delete roles");
            
            // Create Permissions - Resource
            Permission resourceRead = createPermissionIfNotExists(permissionRepository,
                    "RESOURCE_READ", "resource", "READ", "Read resources");
            Permission resourceWrite = createPermissionIfNotExists(permissionRepository,
                    "RESOURCE_WRITE", "resource", "WRITE", "Create and update resources");
            Permission resourceDelete = createPermissionIfNotExists(permissionRepository,
                    "RESOURCE_DELETE", "resource", "DELETE", "Delete resources");
            
            // Create Permissions - System Admin (SaaS level)
            Permission systemManage = createPermissionIfNotExists(permissionRepository,
                    "SYSTEM_MANAGE", "system", "MANAGE", "Manage system-wide settings");
            Permission fullAccess = createPermissionIfNotExists(permissionRepository,
                    "FULL_ACCESS", "*", "*", "Full access to all resources and actions");

            // Create Roles
            Role superAdminRole = createRoleIfNotExists(roleRepository, "SUPER_ADMIN", 
                    "Super Administrator with full access to all resources across all organizations (SaaS level)");
            Role adminRole = createRoleIfNotExists(roleRepository, "ADMIN", 
                    "Administrator with full access (SaaS level - for platform administrators)");
            Role organizationAdminRole = createRoleIfNotExists(roleRepository, "ORGANIZATION_ADMIN", 
                    "Organization Administrator - Full access to manage organization and all sub-organizations");
            Role userRole = createRoleIfNotExists(roleRepository, "USER", "Standard user with basic access");
            Role managerRole = createRoleIfNotExists(roleRepository, "MANAGER", "Manager with elevated access");
            Role individualRole = createRoleIfNotExists(roleRepository, "INDIVIDUAL", 
                    "Individual user (particulier) - Can manage their own properties and receive messages");

            // Assign permissions to roles
            // SUPER_ADMIN: All permissions - Full access to everything
            Set<Permission> allPermissions = Set.of(
                    userRead, userWrite, userDelete,
                    organizationRead, organizationWrite, organizationDelete,
                    propertyRead, propertyWrite, propertyDelete,
                    documentRead, documentWrite, documentDelete,
                    workflowRead, workflowWrite, workflowDelete, workflowApprove,
                    billingRead, billingWrite, billingManage,
                    auditRead, auditManage,
                    notificationRead, notificationWrite,
                    contactRead, contactWrite, contactDelete,
                    roleRead, roleWrite, roleDelete,
                    resourceRead, resourceWrite, resourceDelete,
                    systemManage, fullAccess
            );
            superAdminRole.getPermissions().addAll(allPermissions);

            // ADMIN (SaaS level): All permissions (except system management)
            adminRole.getPermissions().addAll(Set.of(
                    userRead, userWrite, userDelete,
                    organizationRead, organizationWrite,
                    propertyRead, propertyWrite, propertyDelete,
                    documentRead, documentWrite, documentDelete,
                    workflowRead, workflowWrite, workflowDelete, workflowApprove,
                    billingRead, billingWrite,
                    auditRead,
                    notificationRead, notificationWrite,
                    contactRead, contactWrite, contactDelete,
                    roleRead, roleWrite,
                    resourceRead, resourceWrite, resourceDelete
            ));

            // ORGANIZATION_ADMIN: Full access to manage organization and sub-organizations
            // This role is for agency administrators who manage their organization and all sub-organizations
            // Can manage users, organizations, roles, and all resources within their organization
            organizationAdminRole.getPermissions().addAll(Set.of(
                    userRead, userWrite, userDelete,  // Can manage users in their organization
                    organizationRead, organizationWrite, organizationDelete,  // Can manage sub-organizations
                    propertyRead, propertyWrite, propertyDelete,
                    documentRead, documentWrite, documentDelete,
                    workflowRead, workflowWrite, workflowDelete, workflowApprove,
                    billingRead, billingWrite,  // Can manage billing for their organization
                    auditRead,
                    notificationRead, notificationWrite,
                    contactRead, contactWrite, contactDelete,
                    roleRead, roleWrite, roleDelete,  // Can manage roles within their organization (create, update, delete)
                    resourceRead, resourceWrite, resourceDelete
            ));

            // MANAGER: Read and write permissions (no delete)
            managerRole.getPermissions().addAll(Set.of(
                    userRead, userWrite,
                    organizationRead, organizationWrite,
                    propertyRead, propertyWrite,
                    documentRead, documentWrite,
                    workflowRead, workflowWrite, workflowApprove,
                    notificationRead, notificationWrite,
                    contactRead, contactWrite,
                    resourceRead, resourceWrite
            ));

            // USER: Read permissions only
            userRole.getPermissions().addAll(Set.of(
                    userRead,
                    organizationRead,
                    propertyRead,
                    documentRead,
                    workflowRead,
                    notificationRead,
                    contactRead,
                    resourceRead
            ));

            // INDIVIDUAL (Particulier): Permissions pour gérer ses propres propriétés et messages
            // Peut créer, modifier et supprimer ses propres propriétés
            // Peut lire et répondre aux messages reçus pour ses propriétés
            // Peut gérer son propre profil
            individualRole.getPermissions().addAll(Set.of(
                    userRead, userWrite,  // Lire et modifier son propre profil
                    propertyRead, propertyWrite, propertyDelete,  // Gérer ses propres propriétés
                    contactRead, contactWrite,  // Lire et répondre aux messages
                    notificationRead,  // Lire ses notifications
                    documentRead, documentWrite  // Gérer les documents de ses propriétés
            ));

            roleRepository.saveAll(Set.of(superAdminRole, adminRole, organizationAdminRole, managerRole, userRole, individualRole));

            // Create default super admin user if not exists
            if (!userRepository.findByEmail("superadmin@viridial.com").isPresent()) {
                User superAdminUser = new User();
                superAdminUser.setEmail("superadmin@viridial.com");
                superAdminUser.setPassword(passwordEncoder.encode("superadmin123"));
                superAdminUser.setFirstName("Super");
                superAdminUser.setLastName("Admin");
                superAdminUser.setEnabled(true);
                superAdminUser.setAccountNonExpired(true);
                superAdminUser.setAccountNonLocked(true);
                superAdminUser.setCredentialsNonExpired(true);
                superAdminUser.setRoles(Set.of(superAdminRole));
                userRepository.save(superAdminUser);
                logger.info("Default super admin user created: superadmin@viridial.com / superadmin123");
            }

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

