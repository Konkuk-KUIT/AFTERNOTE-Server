package com.example.afternote.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 역할")
public enum UserRole {
    @Schema(description = "일반 사용자")
    USER,
    @Schema(description = "관리자 - 인증 요청 검토 및 승인/거절 권한 보유")
    ADMIN
}
