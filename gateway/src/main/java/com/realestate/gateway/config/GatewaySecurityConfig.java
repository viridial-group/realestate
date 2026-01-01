package com.realestate.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de sécurité spécifique pour le Gateway
 * 
 * Cette configuration désactive complètement l'authentification HTTP Basic
 * et permet l'accès public à toutes les routes API.
 * 
 * Cette configuration remplace celle du common pour le gateway.
 * Utilise @Primary pour s'assurer qu'elle est utilisée en priorité.
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    @Primary // Priorité sur la configuration du common
    @Order(-1) // Priorité élevée pour s'assurer que cette config est appliquée en premier
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // Désactiver complètement la sécurité par défaut
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable()) // CORS géré par GatewayCorsConfig
            .authorizeExchange(exchanges -> exchanges
                // Actuator endpoints publics
                .pathMatchers("/actuator/**").permitAll()
                // Routes API publiques - IMPORTANT: doit être avant anyExchange()
                .pathMatchers("/api/**").permitAll()
                // Swagger/OpenAPI publics
                .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // Toutes les autres routes sont publiques
                .anyExchange().permitAll()
            )
            // Désactiver toutes les authentifications - CRITIQUE
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())
            .logout(logout -> logout.disable());
        
        return http.build();
    }
}

