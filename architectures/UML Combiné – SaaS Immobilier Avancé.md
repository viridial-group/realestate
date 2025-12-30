classDiagram
    %% ========================
    %% Organisation & Hierarchie
    %% ========================
    class Organization {
        id
        name
        type
        parent_id
        plan
        status
    }

    class Team {
        id
        name
        manager_id
        organization_id
    }

    class OrganizationRole {
        id
        organization_id
        role_id
        name
        description
        is_custom
    }

    class OrganizationUser {
        id
        organization_id
        user_id
        role_id
        team_id
        joined_at
    }

    %% ========================
    %% Users & Identity
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

    class RoleDelegation {
        id
        from_user_id
        to_user_id
        role_id
        starts_at
        ends_at
    }

    %% ========================
    %% Resource & Property
    %% ========================
    class Domain {
        id
        name
        organization_id
    }

    class Tag {
        id
        type
        name
    }

    class Resource {
        id
        domain_id
        organization_id
        assigned_user_id
        assigned_team_id
        type
        status
        metadata json
        created_at
        updated_at
    }

    class ResourceAccess {
        id
        resource_id
        user_id
        organization_id
        permission
        expires_at
        granted_by
        created_at
    }

    class Property {
        id
        organization_id
        team_id
        assigned_user_id
        reference
        title
        type
        status
        price
        currency
        surface
        rooms
        city
        country
        latitude
        longitude
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
        created_at
    }

    %% ========================
    %% Document & Media
    %% ========================
    class Document {
        id
        resource_id
        property_id
        name
        type
        url
        size
        metadata json
        created_at
        updated_at
    }

    class Storage {
        id
        type
        endpoint
        bucket
    }

    %% ========================
    %% Workflow & Approvals
    %% ========================
    class ApprovalWorkflow {
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

    %% ========================
    %% Audit & Logging
    %% ========================
    class AuditLog {
        id
        actor_id
        organization_id
        action
        target_type
        target_id
        ip
        created_at
    }

    %% ========================
    %% Notification & Email
    %% ========================
    class Notification {
        id
        type
        message
        recipient_id
        status
        sent_at
    }

    class Email {
        id
        template_id
        recipient_id
        subject
        body
        status
        sent_at
    }

    %% ========================
    %% Associations
    %% ========================
    User "1" --> "*" OrganizationUser : memberships
    Organization "1" --> "*" OrganizationUser : members
    Organization "1" --> "*" OrganizationRole : roles
    Team "1" --> "*" OrganizationUser : members
    Role "1" --> "*" RolePermission
    Permission "1" --> "*" RolePermission
    RoleDelegation "1" --> User : from_user
    RoleDelegation "1" --> User : to_user

    Domain "1" --> "*" Resource
    Tag "1" --> "*" Resource
    Resource "1" --> "*" ResourceAccess
    Property "1" --> "*" PropertyAccess
    Property "1" --> "*" Document
    Resource "1" --> "*" Document
    Document --> Storage
    ApprovalWorkflow "1" --> "*" Task
    Property --> "*" AuditLog
    Resource --> "*" AuditLog
    Document --> "*" AuditLog
    Property --> "*" Notification
    Resource --> "*" Notification
    Email --> Notification
