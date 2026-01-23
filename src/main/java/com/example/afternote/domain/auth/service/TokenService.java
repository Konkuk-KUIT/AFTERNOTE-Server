package com.example.afternote.domain.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final RedisTemplate<String, Long> redisTemplate;

    public TokenService(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token 저장 (예: 7일간 유효)
    // Key를 "RT:사용자ID" 또는 "RT:이메일" 형태로 지정하여 구분하기 쉽게 함
    public void saveToken(String refreshToken, Long userId) {
        redisTemplate.opsForValue()
                     .set("RT:"+refreshToken,userId, 7, TimeUnit.DAYS);
    }

    //조회
    public Long getUserId(String refreshToken) {
        return redisTemplate.opsForValue().get("RT:" + refreshToken);
    }
    
    // 원자적 조회 및 삭제 (TOCTOU 방지)
    // reissue 시 동시성 문제를 방지하기 위해 사용
    public Long getUserIdAndDelete(String refreshToken) {
        return redisTemplate.opsForValue().getAndDelete("RT:" + refreshToken);
    }
    
    // 로그아웃 시 Refresh Token 삭제
    public void deleteToken(String refreshToken) {
        redisTemplate.delete("RT:" + refreshToken);
    }
}
