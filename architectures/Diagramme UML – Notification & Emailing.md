classDiagram
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

    Notification --> Email
    Property --> "*" Notification
    Resource --> "*" Notification
