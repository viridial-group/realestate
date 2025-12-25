package com.realestate.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Configuration des Security Headers pour tous les microservices
 * Ajoute les headers de sécurité recommandés (HSTS, CSP, X-Frame-Options, etc.)
 */
@Configuration
public class SecurityHeadersConfig {

    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
                
                // X-Content-Type-Options: Empêche le MIME type sniffing
                response.setHeader("X-Content-Type-Options", "nosniff");
                
                // X-Frame-Options: Empêche le clickjacking
                response.setHeader("X-Frame-Options", "DENY");
                
                // X-XSS-Protection: Protection XSS (legacy, mais toujours utile)
                response.setHeader("X-XSS-Protection", "1; mode=block");
                
                // Referrer-Policy: Contrôle les informations de referrer
                response.setHeader("Referrer-Policy", ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN.getPolicy());
                
                // Permissions-Policy: Contrôle les fonctionnalités du navigateur
                response.setHeader("Permissions-Policy", 
                    "geolocation=(), microphone=(), camera=()");
                
                // Content-Security-Policy: Protection contre XSS et injection
                response.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self' data:; " +
                    "connect-src 'self' https:;");
                
                // Strict-Transport-Security (HSTS): Force HTTPS (uniquement en production)
                // Décommenter en production avec HTTPS
                // response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                
                filterChain.doFilter(request, response);
            }
        };
    }
}

