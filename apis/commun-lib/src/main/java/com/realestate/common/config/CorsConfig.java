package com.realestate.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration  CORS pour tous les microservices
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Autoriser les origines (à configurer selon l'environnement)
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",  // React dev
            "http://localhost:8080",  // API Gateway
            "https://yourdomain.com"  // Production
        ));
        
        // Autoriser les méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Autoriser les headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers", "Accept-Language"
        ));
        
        // Autoriser les credentials
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests
        configuration.setMaxAge(3600L);
        
        // Exposer les headers personnalisés
        configuration.setExposedHeaders(Arrays.asList(
            "X-Total-Count", "X-Page-Number", "X-Page-Size"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

