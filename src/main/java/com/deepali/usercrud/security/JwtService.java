package com.deepali.usercrud.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key; private final long expirationMs;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expirationMs = expirationMs;
    }
    public String generate(UserDetails user) {
        long now = System.currentTimeMillis();
        return Jwts.builder().subject(user.getUsername()).issuedAt(new Date(now))
            .expiration(new Date(now + expirationMs)).signWith(key).compact();
    }
    public String extractUsername(String token) { return parse(token).getPayload().getSubject(); }
    public boolean isValid(String token, UserDetails user) {
        try { return user.getUsername().equals(extractUsername(token)) && parse(token).getPayload().getExpiration().after(new Date()); }
        catch (JwtException | IllegalArgumentException ex) { return false; }
    }
    private Jws<Claims> parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token); }
    public long expirationSeconds() { return expirationMs / 1000; }
}