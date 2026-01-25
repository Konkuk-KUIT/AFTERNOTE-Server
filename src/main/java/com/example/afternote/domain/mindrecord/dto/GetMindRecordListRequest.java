package com.example.afternote.domain.mindrecord.dto;

import com.example.afternote.domain.mindrecord.model.MindRecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetMindRecordListRequest {

    @Schema(description = "기록 유형", example = "DIARY", nullable = true)
    private MindRecordType type;

    @Schema(description = "조회 화면 타입", example = "LIST", nullable = true)
    private String view; // LIST, CALENDAR

    @Schema(description = "연도 (캘린더 조회 시)", example = "2026", nullable = true)
    private Integer year;

    @Schema(description = "월 (캘린더 조회 시)", example = "1", nullable = true)
    private Integer month;

    @Schema(description = "특정 날짜 조회 (LIST 조회 시)", example = "2026-01-23", nullable = true)
    private String date;
}