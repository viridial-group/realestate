package com.realestate.identity.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Point d'entrée d'authentification personnalisé
 * Empêche l'envoi du header WWW-Authenticate qui déclenche la popup du navigateur
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        
        // Ne pas envoyer WWW-Authenticate header pour éviter la popup du navigateur
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        // S'assurer que le header WWW-Authenticate n'est pas présent
        response.setHeader("WWW-Authenticate", "");
        response.getWriter().write(
            "{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}"
        );
        response.getWriter().flush();
    }
}

