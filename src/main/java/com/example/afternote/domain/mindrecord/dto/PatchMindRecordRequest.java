package com.example.afternote.domain.mindrecord.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "마음의 기록 수정 요청")
public class PatchMindRecordRequest {

    @Schema(description = "기록 제목", example = "수정된 제목", nullable = true)
    private String title;

    @Schema(description = "기록 내용 (빈 문자열 가능)", example = "내용을 수정했습니다.", nullable = false)
    private String content;

    @Schema(description = "기록 날짜", example = "2026-01-12", nullable = false)
    private String date;

    @Schema(description = "임시저장 여부", example = "false", nullable = false)
    private Boolean isDraft;

    @Schema(description = "데일리 질문 ID (DAILY_QUESTION 타입일 때만)", example = "3", nullable = true)
    private Long questionId;

    @Schema(description = "깊은 생각 카테고리 (DEEP_THOUGHT 타입일 때만)", example = "새로운 관점", nullable = true)
    private String category;
}