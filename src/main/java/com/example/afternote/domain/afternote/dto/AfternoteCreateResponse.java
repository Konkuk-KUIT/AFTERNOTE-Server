package com.example.afternote.domain.afternote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AfternoteCreateResponse {

    @Schema(description = "생성된 애프터노트 ID", example = "3")
    private Long afternoteId;
}
