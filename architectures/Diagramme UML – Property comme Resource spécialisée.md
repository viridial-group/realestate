classDiagram
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

    Property "1" --> "*" PropertyAccess
