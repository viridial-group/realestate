package com.realestate.identity.dto;

/**
 * DTO for password hash generation response
 */
public class PasswordHashResponse {

    private String password;
    private String hash;
    private String algorithm;

    public PasswordHashResponse() {
    }

    public PasswordHashResponse(String password, String hash, String algorithm) {
        this.password = password;
        this.hash = hash;
        this.algorithm = algorithm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}

