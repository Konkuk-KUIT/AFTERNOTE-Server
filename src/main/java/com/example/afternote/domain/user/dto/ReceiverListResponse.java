package com.example.afternote.domain.user.dto;

import com.example.afternote.domain.receiver.model.Receiver;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReceiverListResponse {

    @Schema(description = "수신인 ID", example = "1")
    private Long receiverId;

    @Schema(description = "수신인 이름", example = "김소희")
    private String name;

    @Schema(description = "수신인과의 관계", example = "딸")
    private String relation;

    @Schema(description = "수신자 인증번호", example = "550e8400-e29b-41d4-a716-446655440000")
    private String authCode;

    public static ReceiverListResponse from(Receiver receiver) {
        return ReceiverListResponse.builder()
                .receiverId(receiver.getId())
                .name(receiver.getName())
                .relation(receiver.getRelation())
                .authCode(receiver.getAuthCode())
                .build();
    }
}