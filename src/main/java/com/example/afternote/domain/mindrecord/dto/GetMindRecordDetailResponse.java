package com.example.afternote.domain.mindrecord.dto;

import com.example.afternote.domain.mindrecord.model.MindRecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetMindRecordDetailResponse {

    @Schema(description = "마음의 기록 ID", example = "3")
    private Long recordId;

    @Schema(description = "기록 타입", example = "DIARY")
    private MindRecordType type;

    @Schema(description = "제목", example = "오늘의 일기")
    private String title;

    @Schema(description = "기록 날짜", example = "2026-01-27")
    private String date;

    @Schema(description = "임시저장 여부", example = "false")
    private Boolean isDraft;

    @Schema(description = "본문 내용 (일기 / 깊은 생각 / 데일리 질문 답변)", nullable = true)
    private String content;

    @Schema(description = "데일리 질문 ID (DAILY_QUESTION 타입일 때)", nullable = true, example = "12")
    private Long questionId;

    @Schema(description = "깊은 생각 카테고리 (DEEP_THOUGHT 타입일 때)", nullable = true, example = "자아성찰")
    private String category;
}