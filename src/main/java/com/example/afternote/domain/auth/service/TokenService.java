package com.example.afternote.domain.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token 저장 (예: 7일간 유효)
    // Key를 "RT:사용자ID" 또는 "RT:이메일" 형태로 지정하여 구분하기 쉽게 함
    public void saveToken(String email, String refreshToken) {
        redisTemplate.opsForValue()
                     .set("RT:" + email, refreshToken, 7, TimeUnit.DAYS);
    }

    //조회
    public String getToken(String email) {
        return redisTemplate.opsForValue().get("RT:" + email);
    }
    
    // 로그아웃 시 Refresh Token 삭제
    public void deleteToken(String email) {
        redisTemplate.delete("RT:" + email);
    }
}
