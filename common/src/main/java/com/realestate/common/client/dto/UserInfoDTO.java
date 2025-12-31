package com.realestate.common.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Set;

/**
 * DTO for user information retrieved from Identity Service
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private Set<String> roleNames;
    private Long organizationId;
}

