package com.example.afternote.domain.receiver.dto;

import com.example.afternote.domain.receiver.model.DeliveryVerification;
import com.example.afternote.domain.receiver.model.VerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "사망확인 서류 제출 응답")
public record DeliveryVerificationResponse(
        @Schema(description = "인증 요청 ID", example = "1")
        Long id,
        @Schema(description = "인증 상태", example = "PENDING")
        VerificationStatus status,
        @Schema(description = "사망진단서 URL")
        String deathCertificateUrl,
        @Schema(description = "가족관계증명서 URL")
        String familyRelationCertificateUrl,
        @Schema(description = "관리자 메모", nullable = true)
        String adminNote,
        @Schema(description = "생성일시")
        LocalDateTime createdAt
) {
    public static DeliveryVerificationResponse from(DeliveryVerification verification) {
        return new DeliveryVerificationResponse(
                verification.getId(),
                verification.getStatus(),
                verification.getDeathCertificateUrl(),
                verification.getFamilyRelationCertificateUrl(),
                verification.getAdminNote(),
                verification.getCreatedAt()
        );
    }
}
