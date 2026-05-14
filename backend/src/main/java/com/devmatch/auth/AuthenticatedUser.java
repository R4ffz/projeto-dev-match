package com.devmatch.auth;

/**
 * Principal injected by JwtAuthenticationFilter into the SecurityContext.
 * Carries the identifying claims extracted from the JWT.
 */
public record AuthenticatedUser(Long id, String email, String role) {}
