package com.example.afternote.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
@Getter
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs; // 리프레시 토큰 시간 추가

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpirationMs,   // 이름 맞춤 (-ms 제거)
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpirationMs  // 리프레시 토큰도 추가
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    // 액세스 토큰 발급
    public String generateAccessToken(Long userId) {
        return generateToken(userId, accessTokenExpirationMs);
    }

    // 리프레시 토큰 발급
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, refreshTokenExpirationMs);
    }

    // 토큰 생성 공통 로직
    private String generateToken(Long userId, long expireMs) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(expireMs);

        return Jwts.builder()
                .header().type("JWT").and()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Long getUserId(String token) {
        String sub = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload().getSubject();

        return Long.parseLong(sub);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // 만료되었거나, 위조되었거나, 형식이 잘못된 경우 등등
            return false;
        }
    }
}