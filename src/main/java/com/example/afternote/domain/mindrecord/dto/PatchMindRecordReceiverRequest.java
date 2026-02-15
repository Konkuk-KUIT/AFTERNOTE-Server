package com.example.afternote.domain.mindrecord.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "마음의 기록 수신인 전달 설정 요청")
public class PatchMindRecordReceiverRequest {

    @Schema(
            description = "해당 수신인에게 기록 전달 여부 (true: 전달, false: 전달 안함)",
            example = "true",
            nullable = false
    )
    @NotNull(message = "전달 여부는 필수입니다.")
    private Boolean enabled;
}