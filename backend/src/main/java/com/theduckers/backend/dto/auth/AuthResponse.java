package com.theduckers.backend.dto.auth;

import java.time.Instant;


//dto/auth/AuthResponse:

public class AuthResponse {

    private String token;
    private Instant expiresAt;

    public AuthResponse(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
