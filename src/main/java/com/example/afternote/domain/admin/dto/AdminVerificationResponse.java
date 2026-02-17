package com.example.afternote.domain.admin.dto;

import com.example.afternote.domain.receiver.model.DeliveryVerification;
import com.example.afternote.domain.receiver.model.VerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "관리자용 인증 요청 응답")
public record AdminVerificationResponse(
        @Schema(description = "인증 요청 ID", example = "1")
        Long id,
        @Schema(description = "발신자 ID", example = "1")
        Long userId,
        @Schema(description = "발신자 이름", example = "김철수")
        String senderName,
        @Schema(description = "발신자 이메일", example = "sender@example.com")
        String senderEmail,
        @Schema(description = "수신자 ID", example = "1")
        Long receiverId,
        @Schema(description = "수신자 이름", example = "김지은")
        String receiverName,
        @Schema(description = "인증 상태", example = "PENDING")
        VerificationStatus status,
        @Schema(description = "사망진단서 URL (Presigned)")
        String deathCertificateUrl,
        @Schema(description = "가족관계증명서 URL (Presigned)")
        String familyRelationCertificateUrl,
        @Schema(description = "관리자 메모", nullable = true)
        String adminNote,
        @Schema(description = "생성일시")
        LocalDateTime createdAt
) {
    public static AdminVerificationResponse from(DeliveryVerification verification,
                                                   String senderName, String senderEmail,
                                                   String receiverName,
                                                   String deathCertPresignedUrl,
                                                   String familyRelationCertPresignedUrl) {
        return new AdminVerificationResponse(
                verification.getId(),
                verification.getUserId(),
                senderName,
                senderEmail,
                verification.getReceiverId(),
                receiverName,
                verification.getStatus(),
                deathCertPresignedUrl,
                familyRelationCertPresignedUrl,
                verification.getAdminNote(),
                verification.getCreatedAt()
        );
    }
}
