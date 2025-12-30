package com.realestate.identity.service;

import com.realestate.identity.dto.AuthResponse;
import com.realestate.identity.dto.LoginRequest;
import com.realestate.identity.dto.RefreshTokenRequest;
import com.realestate.identity.dto.RegisterRequest;
import com.realestate.identity.entity.User;
import com.realestate.identity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEnabled(true);
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Smith");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void testRegister_Success() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(anyString())).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtService.getExpiration()).thenReturn(86400L);

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(86400L, response.getExpiresIn());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(testUser.getEmail());
        verify(jwtService).generateRefreshToken(testUser.getEmail());
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getEmail());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(anyString())).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtService.getExpiration()).thenReturn(86400L);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(userRepository).save(testUser);
        assertNotNull(testUser.getLastLoginAt());
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void testRefreshToken_Success() {
        // Given
        String refreshToken = "validRefreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn(testUser.getEmail());
        when(jwtService.validateToken(refreshToken, testUser.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(anyString())).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("newRefreshToken");
        when(jwtService.getExpiration()).thenReturn(86400L);

        // When
        AuthResponse response = authService.refreshToken(request);

        // Then
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
        verify(jwtService).isRefreshToken(refreshToken);
        verify(jwtService).extractUsername(refreshToken);
        verify(jwtService).validateToken(refreshToken, testUser.getEmail());
    }

    @Test
    void testRefreshToken_InvalidRefreshToken() {
        // Given
        String refreshToken = "invalidRefreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        when(jwtService.isRefreshToken(refreshToken)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> authService.refreshToken(request));
        verify(jwtService).isRefreshToken(refreshToken);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void testRefreshToken_ExpiredToken() {
        // Given
        String refreshToken = "expiredRefreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn(testUser.getEmail());
        when(jwtService.validateToken(refreshToken, testUser.getEmail())).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> authService.refreshToken(request));
        verify(jwtService).validateToken(refreshToken, testUser.getEmail());
    }

    @Test
    void testRefreshToken_UserDisabled() {
        // Given
        String refreshToken = "validRefreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        testUser.setEnabled(false);
        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn(testUser.getEmail());
        when(jwtService.validateToken(refreshToken, testUser.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> authService.refreshToken(request));
    }

    @Test
    void testLogout_Success() {
        // Given
        String token = "validToken";
        when(jwtService.extractUsername(token)).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When
        assertDoesNotThrow(() -> authService.logout(token));

        // Then
        verify(jwtService).extractUsername(token);
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void testLogout_UserNotFound() {
        // Given
        String token = "validToken";
        when(jwtService.extractUsername(token)).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> authService.logout(token));
    }
}

