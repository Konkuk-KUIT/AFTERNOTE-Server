package com.example.afternote.domain.mindrecord.emotion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmotionCacheService {
    
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SUMMARY_TEXT_KEY = "emotion:summary:text:%d";
    private static final long CACHE_TTL_SECONDS = 86400; // 1일


    /**
     * Redis에 요약 텍스트 저장 (TTL: 1일)
     */
    public void saveSummaryText(Long userId, String summaryText) {
        try {
            String key = String.format(SUMMARY_TEXT_KEY, userId);
            redisTemplate.opsForValue().set(key, summaryText, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Redis 저장 실패 무시
        }
    }

    /**
     * Redis에서 요약 텍스트 조회
     */
    public String getEmotionSummaryText(Long userId) {
        try {
            String key = String.format(SUMMARY_TEXT_KEY, userId);
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }
}
