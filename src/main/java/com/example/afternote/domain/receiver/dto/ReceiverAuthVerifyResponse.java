package com.example.afternote.domain.receiver.dto;

import com.example.afternote.domain.receiver.model.Receiver;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수신자 인증번호 검증 응답")
public record ReceiverAuthVerifyResponse(
        @Schema(description = "수신자 ID", example = "1")
        Long receiverId,
        @Schema(description = "수신자 이름", example = "김지은")
        String receiverName,
        @Schema(description = "발신자 이름", example = "김철수")
        String senderName,
        @Schema(description = "관계", example = "딸")
        String relation
) {
    public static ReceiverAuthVerifyResponse from(Receiver receiver, String senderName) {
        return new ReceiverAuthVerifyResponse(
                receiver.getId(),
                receiver.getName(),
                senderName,
                receiver.getRelation()
        );
    }
}
