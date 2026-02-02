package com.example.afternote.domain.receiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "수신한 타임레터 목록 응답")
@Getter
@Builder
@AllArgsConstructor
public class ReceivedTimeLetterListResponse {

    @Schema(description = "타임레터 목록")
    private List<ReceivedTimeLetterResponse> timeLetters;

    @Schema(description = "총 개수", example = "5")
    private int totalCount;

    public static ReceivedTimeLetterListResponse from(List<ReceivedTimeLetterResponse> timeLetters) {
        return ReceivedTimeLetterListResponse.builder()
                .timeLetters(timeLetters)
                .totalCount(timeLetters.size())
                .build();
    }
}
