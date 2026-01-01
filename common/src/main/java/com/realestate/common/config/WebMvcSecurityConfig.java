package com.realestate.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuration de sécurité pour les services Spring MVC (non-Gateway)
 * 
 * Cette configuration permet l'accès public aux endpoints Actuator
 * et peut être personnalisée par chaque service qui l'utilise.
 */
@Configuration
@EnableWebSecurity
public class WebMvcSecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public WebMvcSecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .httpBasic(httpBasic -> httpBasic.disable()) // Désactiver HTTP Basic Auth
            .formLogin(formLogin -> formLogin.disable()) // Désactiver form login
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

