package com.example.afternote.domain.mindrecord.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostMindRecordResponse {
    @Schema(description = "생성된 마음의 기록 ID", example = "15")
    private Long recordId;
}