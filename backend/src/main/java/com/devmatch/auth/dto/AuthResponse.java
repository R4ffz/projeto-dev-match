package com.devmatch.auth.dto;

public record AuthResponse(
    String token,
    String tokenType,
    long expiresInHours,
    UserSummary user
) {
    public static AuthResponse bearer(String token, long expiresInHours, UserSummary user) {
        return new AuthResponse(token, "Bearer", expiresInHours, user);
    }
}
