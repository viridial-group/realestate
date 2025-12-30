package com.realestate.identity.controller;

import com.realestate.identity.dto.UserDTO;
import com.realestate.identity.entity.User;
import com.realestate.identity.mapper.UserMapper;
import com.realestate.identity.service.JwtService;
import com.realestate.identity.service.UserService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/identity/users")
@Tag(name = "Users", description = "User management API for retrieving, updating, and deleting user information")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public UserController(UserService userService, JwtService jwtService, UserMapper userMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the authenticated user's information based on the JWT token")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(token);
            User user = userService.getCurrentUser(email);
            return ResponseEntity.ok(userMapper.toDTO(user));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns user information for a specific user ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping
    @Operation(summary = "List all users", description = "Returns a list of all users in the system")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates user information for a specific user ID")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user from the database by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

