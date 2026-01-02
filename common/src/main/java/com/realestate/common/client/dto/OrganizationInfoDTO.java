package com.realestate.common.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO for organization information retrieved from Identity Service
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationInfoDTO {
    private Long id;
    private String name;
    private String description;
    private String domain;
    private Boolean active;
}

