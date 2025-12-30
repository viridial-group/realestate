classDiagram
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

    Domain "1" --> "*" Resource
    Tag "1" --> "*" Resource
    Resource "1" --> "*" ResourceAccess
