package com.smartcommunity.admin.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    private final SecretKey key;
    private final long expiration;

    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(Integer userId, String phone) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("phone", phone)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    public Integer getUserId(String token) {
        return Integer.parseInt(Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject());
    }
}
