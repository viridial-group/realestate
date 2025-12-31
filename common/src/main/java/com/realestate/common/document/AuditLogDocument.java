package com.realestate.common.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * Document Elasticsearch pour les Audit Logs
 * Indexé automatiquement via Kafka consumers
 */
@Data
@Document(indexName = "audit-logs")
public class AuditLogDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long organizationId;

    @Field(type = FieldType.Long)
    private Long actorId; // User ID

    @Field(type = FieldType.Keyword)
    private String action; // CREATE, UPDATE, DELETE, VIEW, etc.

    @Field(type = FieldType.Keyword)
    private String targetType; // Property, Document, User, etc.

    @Field(type = FieldType.Long)
    private Long targetId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String ipAddress;

    @Field(type = FieldType.Text)
    private String userAgent;

    @Field(type = FieldType.Date)
    private LocalDateTime timestamp;

    @Field(type = FieldType.Text)
    private String metadata; // JSON string

    @Field(type = FieldType.Keyword)
    private String status; // SUCCESS, FAILURE, PARTIAL
}

