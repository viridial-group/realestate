package com.realestate.identity.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for generating password hashes
 */
@Service
public class PasswordHashService {

    private final PasswordEncoder passwordEncoder;

    public PasswordHashService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generate a BCrypt hash for the given password
     *
     * @param password the plain text password
     * @return the BCrypt hash
     */
    public String generateHash(String password) {
        return passwordEncoder.encode(password);
    }
}

