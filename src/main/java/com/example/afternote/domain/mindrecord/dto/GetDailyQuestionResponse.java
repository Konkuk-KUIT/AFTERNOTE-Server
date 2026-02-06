package com.example.afternote.domain.mindrecord.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetDailyQuestionResponse {

    @Schema(description = "데일리 질문 ID", example = "12", nullable = false)
    private Long questionId;

    @Schema(description = "질문 내용", example = "오늘 가장 기억에 남는 순간은 무엇이었나요?", nullable = false)
    private String content;

}