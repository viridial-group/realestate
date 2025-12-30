classDiagram
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

    Document --> Storage
    Property --> "*" Document
    Resource --> "*" Document
