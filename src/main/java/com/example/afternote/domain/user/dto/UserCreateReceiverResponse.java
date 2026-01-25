package com.example.afternote.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수신자 등록 응답")
public record UserCreateReceiverResponse(
        @Schema(description = "생성된 수신자 ID", example = "1")
        Long receiverId
) {
    public static UserCreateReceiverResponse from(Long receiverId) {
        return new UserCreateReceiverResponse(receiverId);
    }
}