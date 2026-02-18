package com.example.afternote.domain.receiver.dto;

import com.example.afternote.domain.mindrecord.emotion.dto.GetEmotionResponse;
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

    @Schema(description = "송신자 감정 키워드 통계 (최근 7일, 상위 4개)")
    private List<GetEmotionResponse.EmotionStat> emotions;

    @Schema(description = "송신자 감정 요약 문장", example = "가족과 함께한 따뜻한 한 주였군요")
    private String emotionSummary;

    public static ReceivedMindRecordListResponse from(
            List<ReceivedMindRecordResponse> mindRecords,
            List<GetEmotionResponse.EmotionStat> emotions,
            String emotionSummary) {
        return ReceivedMindRecordListResponse.builder()
                .mindRecords(mindRecords)
                .totalCount(mindRecords.size())
                .emotions(emotions)
                .emotionSummary(emotionSummary)
                .build();
    }
}
