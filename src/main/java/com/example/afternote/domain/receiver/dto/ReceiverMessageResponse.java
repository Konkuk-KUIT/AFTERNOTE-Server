package com.example.afternote.domain.receiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수신자 메시지 조회 응답")
public record ReceiverMessageResponse(
        @Schema(description = "발신자 이름", example = "김철수")
        String senderName,
        @Schema(description = "메시지 내용", example = "사랑하는 딸에게...", nullable = true)
        String message
) {
}
