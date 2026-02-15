package com.example.afternote.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "관리자 인증 처리 요청")
public class AdminVerificationActionRequest {

    @Schema(description = "관리자 메모", example = "서류 확인 완료", nullable = true)
    private String adminNote;
}
