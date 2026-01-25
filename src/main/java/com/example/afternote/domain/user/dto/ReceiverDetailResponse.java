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

    @Schema(description = "수신인 이름", example = "김소희")
    private String name;

    @Schema(description = "수신인과의 관계", example = "딸")
    private String relation;

    @Schema(description = "전화번호", example = "010-1234-1234", nullable = true)
    private String phone;

    @Schema(description = "이메일", example = "afternote123@naver.com", nullable = true)
    private String email;

    @Schema(description = "데일리 질문 답변 개수", example = "8")
    private Integer dailyQuestionCount;

    @Schema(description = "타임레터 개수", example = "12")
    private Integer timeLetterCount;

    @Schema(description = "애프터노트 개수", example = "4")
    private Integer afterNoteCount;

    public static ReceiverDetailResponse from(
            Receiver receiver,
            int dailyQuestionCount,
            int timeLetterCount,
            int afterNoteCount
    ) {
        return ReceiverDetailResponse.builder()
                .receiverId(receiver.getId())
                .name(receiver.getName())
                .relation(receiver.getRelation())
                .phone(receiver.getPhone())
                .email(receiver.getEmail())
                .dailyQuestionCount(dailyQuestionCount)
                .timeLetterCount(timeLetterCount)
                .afterNoteCount(afterNoteCount)
                .build();
    }
}