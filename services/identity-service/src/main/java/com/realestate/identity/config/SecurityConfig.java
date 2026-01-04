package com.realestate.identity.config;

import com.realestate.identity.filter.JwtAuthenticationFilter;
import com.realestate.identity.filter.RemoveWwwAuthenticateFilter;
import com.realestate.identity.filter.RolePermissionFilter;
import com.realestate.identity.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RolePermissionFilter rolePermissionFilter;
    private final RemoveWwwAuthenticateFilter removeWwwAuthenticateFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RolePermissionFilter rolePermissionFilter,
            RemoveWwwAuthenticateFilter removeWwwAuthenticateFilter,
            CorsConfigurationSource corsConfigurationSource,
            CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rolePermissionFilter = rolePermissionFilter;
        this.removeWwwAuthenticateFilter = removeWwwAuthenticateFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasic -> httpBasic.disable()) // Désactiver HTTP Basic Auth
                .formLogin(formLogin -> formLogin.disable()) // Désactiver form login
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint) // Point d'entrée personnalisé
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/identity/auth/**").permitAll()
                        .requestMatchers("/api/identity/utils/**").permitAll() // Password hash generation utility
                        .requestMatchers("/api/public/organizations/**").permitAll() // Public organizations endpoint
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(removeWwwAuthenticateFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rolePermissionFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

