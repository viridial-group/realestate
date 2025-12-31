package com.realestate.common.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO for domain information retrieved from Resource Service
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DomainInfoDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Long organizationId;
}

