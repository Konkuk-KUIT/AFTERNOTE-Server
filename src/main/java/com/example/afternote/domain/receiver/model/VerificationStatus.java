package com.example.afternote.domain.receiver.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사망확인 서류 인증 상태")
public enum VerificationStatus {
    @Schema(description = "대기 중 - 관리자 검토 대기")
    PENDING,
    @Schema(description = "승인 - 서류 검증 완료, 콘텐츠 열람 가능")
    APPROVED,
    @Schema(description = "거절 - 서류 검증 실패, 재제출 필요")
    REJECTED
}
