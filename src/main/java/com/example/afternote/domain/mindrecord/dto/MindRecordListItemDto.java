package com.example.afternote.domain.mindrecord.dto;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MindRecordListItemDto {

    @Schema(description = "기록 ID", example = "12")
    private Long recordId;

    @Schema(description = "기록 유형", example = "DIARY")
    private String type;

    @Schema(description = "기록 제목", example = "오늘의 기록")
    private String title;

    @Schema(description = "기록 날짜", example = "2026-01-11")
    private String date;

    @Schema(description = "임시저장 여부", example = "false")
    private Boolean isDraft;

    public static MindRecordListItemDto from(MindRecord record) {
        return MindRecordListItemDto.builder()
                .recordId(record.getId())
                .type(record.getType().name())
                .title(record.getTitle())
                .date(record.getRecordDate().toString())
                .isDraft(record.getIsDraft())
                .build();
    }
}