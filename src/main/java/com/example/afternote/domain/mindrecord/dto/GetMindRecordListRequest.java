package com.example.afternote.domain.mindrecord.dto;

import com.example.afternote.domain.mindrecord.model.MindRecordType;
import com.example.afternote.domain.mindrecord.model.ViewType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMindRecordListRequest {

    @Schema(description = "기록 유형, 없으면 전체", example = "DIARY", nullable = true)
    private MindRecordType type;

    @Schema(description = "조회 화면 타입", example = "LIST", nullable = true)
    private ViewType view; // LIST, CALENDAR

    @Schema(description = "연도 (캘린더 조회 시)", example = "2026", nullable = true)
    private Integer year;

    @Schema(description = "월 (캘린더 조회 시)", example = "1", nullable = true)
    private Integer month;

    public boolean isCalendarView() {
        return view == ViewType.CALENDAR;
    }

    public boolean isAllType() {
        return type == null;
    }
}