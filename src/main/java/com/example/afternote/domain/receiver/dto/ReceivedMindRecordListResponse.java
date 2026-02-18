package com.example.afternote.domain.receiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "수신한 마인드레코드 목록 응답")
@Getter
@Builder
@AllArgsConstructor
public class ReceivedMindRecordListResponse {

    @Schema(description = "마인드레코드 목록")
    private List<ReceivedMindRecordResponse> mindRecords;

    @Schema(description = "총 개수", example = "10")
    private int totalCount;

    public static ReceivedMindRecordListResponse from(List<ReceivedMindRecordResponse> mindRecords) {
        return ReceivedMindRecordListResponse.builder()
                .mindRecords(mindRecords)
                .totalCount(mindRecords.size())
                .build();
    }
}
