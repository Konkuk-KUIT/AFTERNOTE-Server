package com.example.afternote.domain.timeletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "타임레터 목록 응답")
@Getter
@Builder
public class TimeLetterListResponse {

    @Schema(description = "타임레터 목록")
    private List<TimeLetterResponse> timeLetters;

    @Schema(description = "총 개수", example = "5")
    private int totalCount;

    public static TimeLetterListResponse from(List<TimeLetterResponse> timeLetters) {
        return TimeLetterListResponse.builder()
                .timeLetters(timeLetters)
                .totalCount(timeLetters.size())
                .build();
    }
}
