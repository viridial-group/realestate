package com.realestate.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.identity.entity.User;
import com.realestate.identity.service.JwtService;
import com.realestate.identity.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEnabled(true);
        return user;
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        // Given
        String token = "validToken";
        String email = "test@example.com";
        User user = createTestUser();

        when(jwtService.extractUsername(token)).thenReturn(email);
        when(userService.getCurrentUser(email)).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/api/identity/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testGetCurrentUser_Unauthorized() throws Exception {
        // Given
        String token = "invalidToken";
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        mockMvc.perform(get("/api/identity/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = createTestUser();
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(get("/api/identity/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        // Given
        Long userId = 999L;
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/identity/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        // Given
        User user1 = createTestUser();
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("another@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");

        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/identity/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[1].email").value("another@example.com"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        User updateDetails = new User();
        updateDetails.setFirstName("UpdatedFirstName");
        updateDetails.setLastName("UpdatedLastName");

        User updatedUser = createTestUser();
        updatedUser.setFirstName("UpdatedFirstName");
        updatedUser.setLastName("UpdatedLastName");

        when(userService.updateUser(userId, updateDetails)).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/identity/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedFirstName"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLastName"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        // Given
        Long userId = 999L;
        User updateDetails = new User();
        when(userService.updateUser(userId, updateDetails))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        mockMvc.perform(put("/api/identity/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/api/identity/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        // Given
        Long userId = 999L;
        doThrow(new RuntimeException("User not found"))
                .when(userService).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/api/identity/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}

