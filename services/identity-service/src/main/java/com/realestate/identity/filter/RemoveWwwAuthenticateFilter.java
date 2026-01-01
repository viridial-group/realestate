package com.realestate.identity.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre pour supprimer le header WWW-Authenticate des réponses
 * Empêche la popup d'authentification du navigateur
 */
@Component
public class RemoveWwwAuthenticateFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // Wrapper pour intercepter et modifier les headers de réponse
        HttpServletResponse wrappedResponse = new HttpServletResponseWrapper(response) {
            @Override
            public void setHeader(String name, String value) {
                // Ne pas ajouter WWW-Authenticate header
                if (!"WWW-Authenticate".equalsIgnoreCase(name)) {
                    super.setHeader(name, value);
                }
            }

            @Override
            public void addHeader(String name, String value) {
                // Ne pas ajouter WWW-Authenticate header
                if (!"WWW-Authenticate".equalsIgnoreCase(name)) {
                    super.addHeader(name, value);
                }
            }
        };
        
        filterChain.doFilter(request, wrappedResponse);
        
        // S'assurer que le header n'est pas présent après le filtrage
        if (response.containsHeader("WWW-Authenticate")) {
            response.setHeader("WWW-Authenticate", "");
        }
    }
}

