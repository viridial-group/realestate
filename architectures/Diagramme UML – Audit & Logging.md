classDiagram
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

    Property --> "*" AuditLog
    Document --> "*" AuditLog
    Resource --> "*" AuditLog
