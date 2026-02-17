package com.example.afternote.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수신자 등록 응답")
public record UserCreateReceiverResponse(
        @Schema(description = "생성된 수신자 ID", example = "1")
        Long receiverId,
        @Schema(description = "수신자 인증번호 (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String authCode
) {
    public static UserCreateReceiverResponse from(Long receiverId, String authCode) {
        return new UserCreateReceiverResponse(receiverId, authCode);
    }
}
