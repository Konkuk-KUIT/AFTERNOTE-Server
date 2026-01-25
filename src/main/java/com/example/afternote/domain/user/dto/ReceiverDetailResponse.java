package com.example.afternote.domain.user.dto;

import com.example.afternote.domain.receiver.model.Receiver;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReceiverDetailResponse {

    @Schema(description = "수신인 ID", example = "1")
    private Long receiverId;

    @Schema(description = "수신인 이름", example = "김지은")
    private String name;

    @Schema(description = "수신인과의 관계", example = "딸")
    private String relation;

    @Schema(description = "전화번호", example = "01012345678", nullable = true)
    private String phone;

    @Schema(description = "이메일", example = "jieun01@naver.com", nullable = true)
    private String email;

    public static ReceiverDetailResponse from(Receiver receiver) {
        return ReceiverDetailResponse.builder()
                .receiverId(receiver.getId())
                .name(receiver.getName())
                .relation(receiver.getRelation())
                .phone(receiver.getPhone())
                .email(receiver.getEmail())
                .build();
    }
}