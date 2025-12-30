package com.realestate.identity.service;

import com.realestate.identity.entity.User;
import com.realestate.identity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEnabled(true);

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");
        anotherUser.setFirstName("Jane");
        anotherUser.setLastName("Smith");
        anotherUser.setEnabled(true);
    }

    @Test
    void testGetCurrentUser_Success() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmailWithRolesAndPermissions(email))
                .thenReturn(Optional.of(testUser));

        // When
        User result = userService.getCurrentUser(email);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmailWithRolesAndPermissions(email);
    }

    @Test
    void testGetCurrentUser_NotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmailWithRolesAndPermissions(email))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUser(email));
        verify(userRepository).findByEmailWithRolesAndPermissions(email);
    }

    @Test
    void testGetUserById_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findByIdWithRoles(userId))
                .thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        verify(userRepository).findByIdWithRoles(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findByIdWithRoles(userId))
                .thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(userId);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByIdWithRoles(userId);
    }

    @Test
    void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser, anotherUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        Long userId = 1L;
        User updateDetails = new User();
        updateDetails.setFirstName("UpdatedFirstName");
        updateDetails.setLastName("UpdatedLastName");
        updateDetails.setEnabled(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser(userId, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("UpdatedFirstName", result.getFirstName());
        assertEquals("UpdatedLastName", result.getLastName());
        assertFalse(result.getEnabled());
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdateUser_PartialUpdate() {
        // Given
        Long userId = 1L;
        User updateDetails = new User();
        updateDetails.setFirstName("UpdatedFirstName");
        // lastName and enabled are null

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser(userId, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("UpdatedFirstName", result.getFirstName());
        // Original values should remain
        assertEquals("Doe", result.getLastName());
        assertTrue(result.getEnabled());
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdateUser_NotFound() {
        // Given
        Long userId = 999L;
        User updateDetails = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.updateUser(userId, updateDetails));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // When
        assertDoesNotThrow(() -> userService.deleteUser(userId));

        // Then
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }
}

