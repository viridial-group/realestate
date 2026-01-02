package com.realestate.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for password hash generation request
 */
public class PasswordHashRequest {

    @NotBlank(message = "Password is required")
    @Size(min = 3, max = 100, message = "Password must be between 3 and 100 characters")
    private String password;

    public PasswordHashRequest() {
    }

    public PasswordHashRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

