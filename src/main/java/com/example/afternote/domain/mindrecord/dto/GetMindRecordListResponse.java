package com.example.afternote.domain.mindrecord.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetMindRecordListResponse {

    @Schema(description = "마음의 기록 목록")
    private List<MindRecordListItemDto> records;

    @Schema(description = "기록이 존재하는 날짜 목록 (캘린더용)",
            example = "[\"2026-01-03\", \"2026-01-11\"]", nullable = true)
    private List<String> markedDates;
}