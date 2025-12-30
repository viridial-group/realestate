package com.realestate.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de sécurité pour Spring Cloud Gateway (WebFlux)
 * 
 * Cette configuration permet l'accès public aux endpoints Actuator
 * et peut être personnalisée par chaque service qui l'utilise.
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // Actuator endpoints publics (pour monitoring)
                .pathMatchers("/actuator/**").permitAll()
                // Routes API publiques pour l'instant (sera sécurisé plus tard avec JWT)
                .pathMatchers("/api/**").permitAll()
                // Toutes les autres routes nécessitent une authentification
                .anyExchange().authenticated()
            );
        
        return http.build();
    }
}

