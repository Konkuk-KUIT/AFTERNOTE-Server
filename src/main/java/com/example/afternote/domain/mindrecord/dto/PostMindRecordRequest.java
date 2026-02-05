package com.example.afternote.domain.mindrecord.dto;

import com.example.afternote.domain.mindrecord.model.MindRecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostMindRecordRequest {

    @NotNull
    @Schema(description = "기록 유형", example = "DIARY", nullable = false)
    private MindRecordType type;

    @NotBlank
    @Schema(description = "기록 제목", example = "오늘의 기록", nullable = true)
    private String title;

    @NotBlank
    @Schema(description = "기록 내용", example = "오늘은 날씨가 정말 좋았다.", nullable = false)
    private String content;

    @NotBlank
    @Schema(description = "기록 날짜 (yyyy-MM-dd)", example = "2026-01-11", nullable = false)
    private String date;

    @NotNull
    @Schema(description = "임시저장 여부", example = "false", nullable = false)
    private Boolean isDraft;

    @Schema(description = "데일리 질문 ID (DAILY_QUESTION일 때만)", example = "3", nullable = true)
    private Long questionId;

    @Schema(description = "깊은 생각 카테고리 (DEEP_THOUGHT일 때만)", example = "나의 가치관", nullable = true)
    private String category;
}