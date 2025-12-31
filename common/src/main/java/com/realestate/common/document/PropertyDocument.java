package com.realestate.common.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Document Elasticsearch pour les Properties
 * Indexé automatiquement via Kafka consumers
 */
@Data
@Document(indexName = "properties")
public class PropertyDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String reference;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String type; // APARTMENT, HOUSE, LAND, COMMERCIAL, etc.

    @Field(type = FieldType.Keyword)
    private String status; // DRAFT, PUBLISHED, SOLD, RENTED, ARCHIVED

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private String currency;

    @Field(type = FieldType.Double)
    private BigDecimal surface;

    @Field(type = FieldType.Integer)
    private Integer rooms;

    @Field(type = FieldType.Integer)
    private Integer bedrooms;

    @Field(type = FieldType.Integer)
    private Integer bathrooms;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String address;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Keyword)
    private String postalCode;

    @Field(type = FieldType.Keyword)
    private String country;

    @Field(type = FieldType.Double)
    private BigDecimal latitude;

    @Field(type = FieldType.Double)
    private BigDecimal longitude;

    @Field(type = FieldType.Long)
    private Long organizationId;

    @Field(type = FieldType.Long)
    private Long teamId;

    @Field(type = FieldType.Long)
    private Long assignedUserId;

    @Field(type = FieldType.Long)
    private Long createdBy;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Boolean)
    private Boolean active = true;
}

