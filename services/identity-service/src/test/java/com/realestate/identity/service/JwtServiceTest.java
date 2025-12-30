package com.realestate.identity.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String SECRET = "mySecretKeyForTestingPurposesOnlyMustBeAtLeast256BitsLong";
    private static final Long EXPIRATION = 86400000L; // 24 hours
    private static final Long REFRESH_EXPIRATION = 604800000L; // 7 days
    private static final String USERNAME = "test@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", REFRESH_EXPIRATION);
    }

    @Test
    void testGenerateToken() {
        // When
        String token = jwtService.generateToken(USERNAME);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateTokenWithExtraClaims() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");

        // When
        String token = jwtService.generateToken(USERNAME, extraClaims);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateRefreshToken() {
        // When
        String refreshToken = jwtService.generateRefreshToken(USERNAME);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(jwtService.isRefreshToken(refreshToken));
    }

    @Test
    void testExtractUsername() {
        // Given
        String token = jwtService.generateToken(USERNAME);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(USERNAME, extractedUsername);
    }

    @Test
    void testValidateToken() {
        // Given
        String token = jwtService.generateToken(USERNAME);

        // When
        Boolean isValid = jwtService.validateToken(token, USERNAME);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithWrongUsername() {
        // Given
        String token = jwtService.generateToken(USERNAME);
        String wrongUsername = "wrong@example.com";

        // When
        Boolean isValid = jwtService.validateToken(token, wrongUsername);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testIsRefreshToken() {
        // Given
        String accessToken = jwtService.generateToken(USERNAME);
        String refreshToken = jwtService.generateRefreshToken(USERNAME);

        // When
        Boolean isAccessTokenRefresh = jwtService.isRefreshToken(accessToken);
        Boolean isRefreshTokenRefresh = jwtService.isRefreshToken(refreshToken);

        // Then
        assertFalse(isAccessTokenRefresh);
        assertTrue(isRefreshTokenRefresh);
    }

    @Test
    void testGetExpiration() {
        // When
        Long expirationInSeconds = jwtService.getExpiration();

        // Then
        assertEquals(EXPIRATION / 1000, expirationInSeconds);
    }

    @Test
    void testExtractExpiration() {
        // Given
        String token = jwtService.generateToken(USERNAME);

        // When
        java.util.Date expiration = jwtService.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new java.util.Date()));
    }
}

