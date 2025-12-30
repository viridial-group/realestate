package com.realestate.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.identity.dto.AuthResponse;
import com.realestate.identity.dto.LoginRequest;
import com.realestate.identity.dto.RefreshTokenRequest;
import com.realestate.identity.dto.RegisterRequest;
import com.realestate.identity.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister_Success() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        AuthResponse response = new AuthResponse("accessToken", "refreshToken", 86400L);
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/identity/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.expiresIn").value(86400L));
    }

    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // When & Then
        mockMvc.perform(post("/api/identity/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("accessToken", "refreshToken", 86400L);
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/identity/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/identity/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("refreshToken");
        AuthResponse response = new AuthResponse("newAccessToken", "newRefreshToken", 86400L);
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/identity/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("newRefreshToken"));
    }

    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("invalidToken");
        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid refresh token"));

        // When & Then
        mockMvc.perform(post("/api/identity/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogout_Success() throws Exception {
        // Given
        String token = "validToken";
        doNothing().when(authService).logout(anyString());

        // When & Then
        mockMvc.perform(post("/api/identity/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}

