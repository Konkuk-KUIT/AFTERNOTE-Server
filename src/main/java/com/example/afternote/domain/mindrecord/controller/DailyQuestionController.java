package com.example.afternote.domain.mindrecord.controller;

import com.example.afternote.domain.mindrecord.dto.GetDailyQuestionResponse;
import com.example.afternote.domain.mindrecord.service.DailyQuestionService;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daily-question")
@RequiredArgsConstructor
public class DailyQuestionController {

    private final DailyQuestionService dailyQuestionService;

    @Operation(
            summary = "오늘의 데일리 질문 조회 API",
            description = "사용자에게 오늘의 데일리 질문 1개를 조회합니다."
    )
    @GetMapping
    public ApiResponse<GetDailyQuestionResponse> getDailyQuestion(
            @Parameter(hidden = true) @UserId Long userId
    ) {
        return ApiResponse.success(
                dailyQuestionService.getTodayDailyQuestion(userId)
        );
    }
}