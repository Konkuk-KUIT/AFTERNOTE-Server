package com.example.afternote.domain.timeletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "타임레터 삭제 요청")
@Getter
@NoArgsConstructor
public class TimeLetterDeleteRequest {

    @Schema(description = "삭제할 타임레터 ID 목록", example = "[1, 2, 3]")
    @NotEmpty(message = "삭제할 타임레터 ID를 입력해주세요.")
    private List<Long> timeLetterIds;
}
