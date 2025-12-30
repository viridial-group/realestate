classDiagram
    class User {
        id
        email
        password_hash
        status
        locale
        timezone
    }

    class Organization {
        id
        name
        type
        parent_id
        plan_id
        status
    }

    class OrganizationMember {
        id
        user_id
        organization_id
        role_id
        team_id
        status
        joined_at
        active_context boolean
    }

    class Team {
        id
        name
        manager_id
        organization_id
    }

    class Role {
        id
        name
        parent_role_id
        is_system
        organization_id
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

    class PermissionScope {
        permission_id
        scope
    }

    class RoleDelegation {
        id
        from_user_id
        to_user_id
        role_id
        starts_at
        ends_at
    }

    User "1" --> "*" OrganizationMember
    Organization "1" --> "*" OrganizationMember
    Organization "1" --> "*" Team
    Role "1" --> "*" OrganizationMember
    Role "1" --> "*" RolePermission
    Permission "1" --> "*" RolePermission
    Permission "1" --> "*" PermissionScope
    RoleDelegation "1" --> User : from_user
    RoleDelegation "1" --> User : to_user
