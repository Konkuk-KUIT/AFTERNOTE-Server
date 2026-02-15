package com.example.afternote.domain.receiver.dto;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.model.MindRecordType;
import com.example.afternote.domain.mindrecord.model.MindRecordReceiver;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "수신한 마인드레코드 응답")
@Getter
@Builder
@AllArgsConstructor
public class ReceivedMindRecordResponse {

    @Schema(description = "마인드레코드 ID", example = "1")
    private Long id;

    @Schema(description = "기록 타입", example = "DIARY")
    private MindRecordType type;

    @Schema(description = "제목", example = "오늘의 일기")
    private String title;

    @Schema(description = "기록 날짜")
    private LocalDate recordDate;

    @Schema(description = "임시저장 여부")
    private Boolean isDraft;

    @Schema(description = "발신자 이름", example = "김철수")
    private String senderName;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    public static ReceivedMindRecordResponse from(MindRecordReceiver mindRecordReceiver) {
        MindRecord mindRecord = mindRecordReceiver.getMindRecord();
        return ReceivedMindRecordResponse.builder()
                .id(mindRecord.getId())
                .type(mindRecord.getType())
                .title(mindRecord.getTitle())
                .recordDate(mindRecord.getRecordDate())
                .isDraft(mindRecord.getIsDraft())
                .senderName(mindRecord.getUser().getName())
                .createdAt(mindRecord.getCreatedAt())
                .build();
    }
}
