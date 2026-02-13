package com.example.afternote.domain.mindrecord.emotion.service;

import com.example.afternote.domain.mindrecord.emotion.dto.GetEmotionResponse;
import com.example.afternote.domain.mindrecord.emotion.model.Emotion;
import com.example.afternote.domain.mindrecord.emotion.repository.EmotionRepository;
import com.example.afternote.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionService {
    
    private final EmotionRepository emotionRepository;
    private final EmotionCacheService emotionCacheService;

    /**
     * 유저의 감정별 비율 계산 (공백으로 분리된 각 감정을 개별 계산)
     * @param userId 대상 유저 ID
     * @return 감정별 비율 리스트 (예: 기쁨 40%, 슬픔 30%, ...)
     */
    public List<GetEmotionResponse.EmotionStat> getEmotionStatistics(Long userId) {
        // 1) 유저의 모든 감정 조회
        List<Emotion> emotions = emotionRepository.findByUserId(userId);
        
        if (emotions.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }
        
        // 2) keyword를 공백으로 분리 후 각 감정별 개수 세기
        Map<String, Long> emotionCounts = emotions.stream()
                .flatMap(emotion -> Arrays.stream(emotion.getKeyword().split(" "))) // 공백으로 분리
                .map(String::trim) // 앞뒤 공백 제거
                .filter(e -> !e.isEmpty()) // 빈 문자열 필터링
                .collect(Collectors.groupingBy(
                        String::valueOf,
                        Collectors.counting()
                ));
        
        // 3) 전체 감정 개수 (분리된 각 감정의 총합)
        long totalCount = emotions.stream()
                .flatMap(emotion -> Arrays.stream(emotion.getKeyword().split(" ")))
                .map(String::trim)
                .filter(e -> !e.isEmpty())
                .count();
        
        // 4) 비율 계산해서 EmotionStat로 변환
        return emotionCounts.entrySet().stream()
                .map(entry -> new GetEmotionResponse.EmotionStat(
                        entry.getKey(),
                        (entry.getValue() * 100.0) / totalCount
                ))
                .sorted((a, b) -> Double.compare(b.percentage(), a.percentage())) // 비율 높은순
                .collect(Collectors.toList());
    }
  
}
