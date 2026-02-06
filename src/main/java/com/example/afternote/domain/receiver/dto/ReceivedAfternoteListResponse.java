package com.example.afternote.domain.receiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "수신한 애프터노트 목록 응답")
@Getter
@Builder
@AllArgsConstructor
public class ReceivedAfternoteListResponse {

    @Schema(description = "애프터노트 목록")
    private List<ReceivedAfternoteResponse> afternotes;

    @Schema(description = "총 개수", example = "3")
    private int totalCount;

    public static ReceivedAfternoteListResponse from(List<ReceivedAfternoteResponse> afternotes) {
        return ReceivedAfternoteListResponse.builder()
                .afternotes(afternotes)
                .totalCount(afternotes.size())
                .build();
    }
}
