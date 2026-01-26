package com.example.afternote.domain.afternote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AfternotePageResponse {

    @Schema(description = "애프터노트 목록")
    private List<AfternoteResponse> content;
    
    @Schema(description = "현재 페이지 번호", example = "0")
    private int page;
    
    @Schema(description = "요청한 사이즈", example = "10")
    private int size;
    
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private boolean hasNext;
}
