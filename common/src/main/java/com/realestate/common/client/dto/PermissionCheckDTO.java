package com.realestate.common.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO for permission check response from Identity Service
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionCheckDTO {
    private Boolean hasPermission;
    private String reason;
}

