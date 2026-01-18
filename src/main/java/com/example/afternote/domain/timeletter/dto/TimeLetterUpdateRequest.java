package com.example.afternote.domain.timeletter.dto;

import com.example.afternote.domain.timeletter.model.TimeLetterStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "타임레터 수정 요청")
@Getter
@NoArgsConstructor
public class TimeLetterUpdateRequest {

    @Schema(description = "제목", example = "미래의 나에게")
    private String title;

    @Schema(description = "내용", example = "1년 후의 나에게 보내는 편지...")
    private String content;

    @Schema(description = "발송 예정 시간", example = "2025-12-31T23:59:59")
    private LocalDateTime sendAt;

    @Schema(description = "상태 (DRAFT: 임시저장, SCHEDULED: 정식등록)")
    private TimeLetterStatus status;

    @Schema(description = "미디어 목록")
    @Valid
    private List<TimeLetterMediaRequest> mediaList;
}
