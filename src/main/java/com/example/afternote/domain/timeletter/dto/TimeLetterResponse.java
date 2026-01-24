package com.example.afternote.domain.timeletter.dto;

import com.example.afternote.domain.timeletter.model.TimeLetter;
import com.example.afternote.domain.timeletter.model.TimeLetterMedia;
import com.example.afternote.domain.timeletter.model.TimeLetterStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "타임레터 응답")
@Getter
@Builder
public class TimeLetterResponse {

    @Schema(description = "타임레터 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "미래의 나에게")
    private String title;

    @Schema(description = "내용", example = "1년 후의 나에게 보내는 편지...")
    private String content;

    @Schema(description = "발송 예정 시간", example = "2025-12-31T23:59:59")
    private LocalDateTime sendAt;

    @Schema(description = "상태")
    private TimeLetterStatus status;

    @Schema(description = "미디어 목록")
    private List<TimeLetterMediaResponse> mediaList;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    public static TimeLetterResponse from(TimeLetter timeLetter, List<TimeLetterMedia> mediaList) {
        return TimeLetterResponse.builder()
                .id(timeLetter.getId())
                .title(timeLetter.getTitle())
                .content(timeLetter.getContent())
                .sendAt(timeLetter.getSendAt())
                .status(timeLetter.getStatus())
                .mediaList(mediaList == null ? List.of() : mediaList.stream()
                        .map(TimeLetterMediaResponse::from)
                        .collect(Collectors.toList()))
                .createdAt(timeLetter.getCreatedAt())
                .updatedAt(timeLetter.getUpdatedAt())
                .build();
    }
}
