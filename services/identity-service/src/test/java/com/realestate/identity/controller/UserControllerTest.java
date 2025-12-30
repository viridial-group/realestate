package com.realestate.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.identity.dto.UserDTO;
import com.realestate.identity.entity.User;
import com.realestate.identity.mapper.UserMapper;
import com.realestate.identity.filter.JwtAuthenticationFilter;
import com.realestate.identity.filter.RolePermissionFilter;
import com.realestate.identity.service.CustomUserDetailsService;
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
import org.springframework.context.annotation.Import;
import com.realestate.common.exception.GlobalExceptionHandler;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private RolePermissionFilter rolePermissionFilter;

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

    private UserDTO createTestUserDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEnabled(true);
        return dto;
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        // Given
        String token = "validToken";
        String email = "test@example.com";
        User user = createTestUser();
        UserDTO userDTO = createTestUserDTO();

        when(jwtService.extractUsername(anyString())).thenReturn(email);
        when(userService.getCurrentUser(email)).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

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
        when(jwtService.extractUsername(anyString())).thenThrow(new RuntimeException("Invalid token"));

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
        UserDTO userDTO = createTestUserDTO();
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        // Le mapper doit être mocké avec le même objet User
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

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
        // Le mapper ne sera pas appelé car l'exception est lancée par orElseThrow() avant

        // When & Then - Le controller lance ResourceNotFoundException qui est géré par GlobalExceptionHandler
        // Note: Si le GlobalExceptionHandler ne fonctionne pas, on peut aussi vérifier le status 500
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

        UserDTO userDTO1 = createTestUserDTO();
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setEmail("another@example.com");
        userDTO2.setFirstName("Jane");
        userDTO2.setLastName("Smith");

        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.toDTO(user1)).thenReturn(userDTO1);
        when(userMapper.toDTO(user2)).thenReturn(userDTO2);
        // Alternative: utiliser any() pour être plus flexible
        when(userMapper.toDTO(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            if (u.getId() == 1L) return userDTO1;
            if (u.getId() == 2L) return userDTO2;
            return createTestUserDTO();
        });

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
        UserDTO updateDTO = new UserDTO();
        updateDTO.setId(userId);
        updateDTO.setFirstName("UpdatedFirstName");
        updateDTO.setLastName("UpdatedLastName");
        updateDTO.setEmail("test@example.com");

        User updateEntity = new User();
        updateEntity.setId(userId);
        updateEntity.setFirstName("UpdatedFirstName");
        updateEntity.setLastName("UpdatedLastName");
        updateEntity.setEmail("test@example.com");

        User updatedUser = createTestUser();
        updatedUser.setFirstName("UpdatedFirstName");
        updatedUser.setLastName("UpdatedLastName");

        UserDTO updatedDTO = createTestUserDTO();
        updatedDTO.setFirstName("UpdatedFirstName");
        updatedDTO.setLastName("UpdatedLastName");

        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(updateEntity);
        when(userService.updateUser(userId, updateEntity)).thenReturn(updatedUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/identity/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("UpdatedFirstName"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLastName"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        // Given
        Long userId = 999L;
        UserDTO updateDTO = new UserDTO();
        updateDTO.setId(userId);
        updateDTO.setEmail("test@example.com");
        User updateEntity = new User();
        updateEntity.setId(userId);
        updateEntity.setEmail("test@example.com");
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(updateEntity);
        when(userService.updateUser(userId, updateEntity))
                .thenThrow(new ResourceNotFoundException("User", userId));

        // When & Then
        mockMvc.perform(put("/api/identity/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);
        // Le controller appelle directement deleteUser sans vérifier l'existence

        // When & Then
        mockMvc.perform(delete("/api/identity/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        // Given
        Long userId = 999L;
        doThrow(new ResourceNotFoundException("User", userId))
                .when(userService).deleteUser(userId);

        // When & Then
        // Le controller catch l'exception et retourne notFound() sans body JSON
        mockMvc.perform(delete("/api/identity/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}

