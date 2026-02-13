package com.example.afternote.domain.mindrecord.emotion.controller;

import com.example.afternote.domain.mindrecord.emotion.dto.GetEmotionResponse;
import com.example.afternote.domain.mindrecord.emotion.service.EmotionService;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import com.example.afternote.global.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "감정 분석", description = "Gemini API를 활용한 감정 키워드 분석")
@RestController
@RequestMapping("/api/v1/emotions")
@RequiredArgsConstructor
public class EmotionController {
    
    private final EmotionService emotionService;
    private final GeminiService geminiService;
    private final UserRepository userRepository;


    @Operation(
            summary = "감정 분석 API",
            description = "사용자에게 최근 7일 간의 마인드 레코드로 감정들과 그것들의 비율을 제공합니다."
    )
    @GetMapping
    public ApiResponse<GetEmotionResponse> getEmotion(
            @Parameter(hidden = true) @UserId Long userId
    ) {
        String answer = geminiService.summaryEmotion(userId);
        List<GetEmotionResponse.EmotionStat> emotionStat = emotionService.getEmotionStatistics(userId);
        return ApiResponse.success(GetEmotionResponse.builder()
                .emotions(emotionStat)
                .summary(answer).build());
    }

}
