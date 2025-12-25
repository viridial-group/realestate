package com.realestate.common.config;

/**
 * Classe de base pour la configuration de sécurité
 * Les services doivent créer leur propre SecurityConfig qui étend cette classe
 * 
 * Exemple d'utilisation :
 * 
 * @Configuration
 * @EnableWebSecurity
 * @EnableMethodSecurity
 * @RequiredArgsConstructor
 * public class SecurityConfig {
 *     private final JwtAuthenticationFilter jwtAuthenticationFilter;
 *     
 *     @Bean
 *     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 *         return http
 *             .csrf(csrf -> csrf.disable())
 *             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
 *             .authorizeHttpRequests(auth -> auth
 *                 .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
 *                 .anyRequest().authenticated()
 *             )
 *             .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
 *             .build();
 *     }
 * }
 * 
 * Note: Cette classe est une classe utilitaire/documentation.
 * Chaque service doit créer sa propre SecurityConfig.
 */
public abstract class SecurityConfigBase {
    // Classe de base pour documentation et structure
    // Les services doivent créer leur propre SecurityConfig
}
