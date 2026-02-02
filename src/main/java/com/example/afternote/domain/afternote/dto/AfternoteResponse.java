package com.example.afternote.domain.afternote.dto;

import com.example.afternote.domain.afternote.model.AfternoteCategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AfternoteResponse {

    @Schema(description = "애프터노트 ID", example = "10")
    private Long afternoteId;
    
    @Schema(description = "제목", example = "인스타그램")
    private String title;
    
    @Schema(description = "카테고리", example = "SOCIAL")
    private AfternoteCategoryType category;
    
    @Schema(description = "생성일시", example = "2025-11-26T14:30:00")
    private LocalDateTime createdAt;
}
