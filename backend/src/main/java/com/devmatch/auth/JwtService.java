package com.devmatch.auth;

import com.devmatch.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final SecretKey key;
    private final Duration expiration;

    public JwtService(
        @Value("${devmatch.security.jwt.secret}") String secret,
        @Value("${devmatch.security.jwt.expiration-hours:24}") long expirationHours
    ) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                "JWT secret must be at least 32 bytes long for HS256; got " + bytes.length);
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expiration = Duration.ofHours(expirationHours);
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(expiration);
        return Jwts.builder()
            .subject(user.getEmail())
            .claim("userId", user.getId())
            .claim("role", user.getRole().name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(key)
            .compact();
    }

    public Optional<Claims> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long getExpirationHours() {
        return expiration.toHours();
    }
}
