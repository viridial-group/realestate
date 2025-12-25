package com.realestate.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

/**
 * Service pour la validation et l'extraction des informations JWT
 * Utilisé par tous les microservices pour valider les tokens
 * Note: La génération de tokens est gérée par user-service uniquement
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Génère un token JWT avec les informations fournies
     */
    public String generateToken(UUID userId, UUID orgId, List<UUID> tenants, 
                                 List<String> roles, Set<String> permissions, 
                                 String preferredLanguage) {
        var builder = Jwts.builder()
                .subject(userId.toString())
                .claim("orgId", orgId.toString())
                .claim("tenants", tenants.stream().map(UUID::toString).toList())
                .claim("roles", roles)
                .claim("permissions", permissions)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs));
        
        // Ajouter la langue préférée si elle existe
        if (preferredLanguage != null && !preferredLanguage.isEmpty()) {
            builder.claim("preferredLanguage", preferredLanguage);
        }
        
        return builder.signWith(getSigningKey())
                .compact();
    }

    /**
     * Valide un token JWT
     */
    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Crée un objet Authentication à partir d'un token JWT
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        UUID userId = UUID.fromString(claims.getSubject());
        UUID orgId = UUID.fromString(claims.get("orgId", String.class));

        List<UUID> tenants = ((List<?>) claims.get("tenants", List.class))
                .stream()
                .map(id -> UUID.fromString(id.toString()))
                .toList();

        @SuppressWarnings("unchecked")
        Set<String> roles = new HashSet<>((List<String>) claims.get("roles", List.class));
        @SuppressWarnings("unchecked")
        Set<String> permissions = new HashSet<>((List<String>) claims.get("permissions", List.class));

        // Extraire la langue préférée si elle existe
        String preferredLanguage = claims.get("preferredLanguage", String.class);

        JwtPrincipal principal = new JwtPrincipal(
                userId,
                orgId,
                tenants,
                roles,
                permissions,
                preferredLanguage
        );

        return new UsernamePasswordAuthenticationToken(
                principal,
                token,
                principal.getAuthorities()
        );
    }

    /**
     * Extrait les claims d'un token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
