package com.realestate.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité pour les services Spring MVC (non-Gateway)
 * 
 * Cette configuration permet l'accès public aux endpoints Actuator
 * et peut être personnalisée par chaque service qui l'utilise.
 */
@Configuration
@EnableWebSecurity
public class WebMvcSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Actuator endpoints publics (pour monitoring)
                .requestMatchers("/actuator/**").permitAll()
                // Routes API publiques pour l'instant (sera sécurisé plus tard avec JWT)
                .requestMatchers("/api/**").permitAll()
                // Toutes les autres routes nécessitent une authentification
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}

