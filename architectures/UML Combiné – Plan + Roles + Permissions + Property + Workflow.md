classDiagram
    %% ========================
    %% ORGANIZATION & PLAN
    %% ========================
    class Organization {
        id
        name
        plan_id
        status
    }

    class Plan {
        id
        name
        features json
        description
    }

    Organization --> Plan : uses

    %% ========================
    %% USERS & RBAC
    %% ========================
    class User {
        id
        email
        password_hash
        status
    }

    class Role {
        id
        name
        description
        is_system
    }

    class Permission {
        id
        key
        module
        description
    }

    class RolePermission {
        role_id
        permission_id
    }

    class OrganizationRole {
        id
        organization_id
        role_id
        allowed_features json
    }

    class OrganizationUser {
        id
        organization_id
        user_id
        role_id
        team_id
        joined_at
    }

    Organization --> "*" OrganizationRole : roles
    Organization --> "*" OrganizationUser : members
    OrganizationRole --> Role : assigns
    Role --> "*" Permission : grants
    OrganizationUser --> OrganizationRole : assigned_role

    %% ========================
    %% RESOURCE & PROPERTY
    %% ========================
    class Domain {
        id
        name
        organization_id
    }

    class Resource {
        id
        domain_id
        organization_id
        type
        status
        metadata json
    }

    class ResourceAccess {
        id
        resource_id
        user_id
        organization_id
        permission
        expires_at
        granted_by
    }

    class Property {
        id
        organization_id
        assigned_user_id
        title
        type
        status
        price
        currency
        created_at
    }

    class PropertyAccess {
        id
        property_id
        user_id
        organization_id
        permission
        expires_at
        granted_by
    }

    Domain "1" --> "*" Resource
    Resource "1" --> "*" ResourceAccess
    Property "1" --> "*" PropertyAccess
    Property --> Resource : inherits

    %% ========================
    %% WORKFLOW & APPROVALS
    %% ========================
    class Workflow {
        id
        action
        steps
        required_roles
    }

    class Task {
        id
        workflow_id
        assigned_to
        status
        due_date
    }

    Property --> "*" Workflow
    Workflow "1" --> "*" Task

    %% ========================
    %% RELATIONS PLAN → FEATURE → ACL
    %% ========================
    OrganizationRole --> Organization : inherits_allowed_features
    ResourceAccess --> Property : controls_access
    PropertyAccess --> Property : controls_access
