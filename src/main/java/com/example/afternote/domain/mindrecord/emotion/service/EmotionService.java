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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionService {

    private final EmotionRepository emotionRepository;
    private final EmotionCacheService emotionCacheService;

    /**
     * 유저의 감정별 비율 계산 (공백으로 분리된 각 감정을 개별 계산)
     *
     * @param userId 대상 유저 ID
     * @return 감정별 비율 리스트 (예: 기쁨 40%, 슬픔 30%, ...)
     */
    public List<GetEmotionResponse.EmotionStat> getEmotionStatistics(Long userId) {
        // 1) 유저의 모든 감정 조회
        List<Emotion> emotions = emotionRepository.findByUserId(userId);

        if (emotions.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }

        // 1) 모든 감정의 개수를 먼저 집계 (Map)
        Map<String, Long> emotionCounts = emotions.stream()
                .flatMap(emotion -> Arrays.stream(emotion.getKeyword().split("\\s+")))
                .map(String::trim)
                .filter(e -> !e.isEmpty())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

// 2) 개수(Value)가 높은 순서대로 상위 4개만 먼저 추출하여 리스트화
        List<Map.Entry<String, Long>> top4Entries = emotionCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(4)
                .collect(Collectors.toList());

// 3) 추출된 '상위 4개만의 합계'를 구함 (이게 분모가 됨)
        long top4TotalCount = top4Entries.stream()
                .mapToLong(Map.Entry::getValue)
                .sum();

// 4) 상위 4개에 대해서만 새로운 합계를 기준으로 비율 계산
        return top4Entries.stream()
                .map(entry -> new GetEmotionResponse.EmotionStat(
                        entry.getKey(),
                        top4TotalCount == 0 ? 0 : (entry.getValue() * 100.0) / top4TotalCount
                ))
                .collect(Collectors.toList());

    }
}
