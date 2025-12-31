package com.realestate.common.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Utility class to extract JWT token from HTTP headers
 */
@Component
public class JwtTokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Extract JWT token from Authorization header
     * @param authorizationHeader The Authorization header value (e.g., "Bearer <token>")
     * @return The JWT token without the "Bearer " prefix, or null if invalid
     */
    public String extractToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return null;
        }

        if (authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }

        return authorizationHeader;
    }
}

