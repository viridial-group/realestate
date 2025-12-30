classDiagram
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

    ApprovalWorkflow "1" --> "*" Task
