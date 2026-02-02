package com.example.afternote.domain.receiver.dto;

import com.example.afternote.domain.afternote.model.Afternote;
import com.example.afternote.domain.afternote.model.AfternoteCategoryType;
import com.example.afternote.domain.afternote.model.AfternoteReceiver;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "수신한 애프터노트 응답")
@Getter
@Builder
@AllArgsConstructor
public class ReceivedAfternoteResponse {

    @Schema(description = "애프터노트 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "내 아들에게")
    private String title;

    @Schema(description = "카테고리", example = "GALLERY")
    private AfternoteCategoryType category;

    @Schema(description = "남기는 메시지")
    private String leaveMessage;

    @Schema(description = "발신자 ID", example = "1")
    private Long senderId;

    @Schema(description = "발신자 이름", example = "김철수")
    private String senderName;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    public static ReceivedAfternoteResponse from(AfternoteReceiver afternoteReceiver, String senderName) {
        Afternote afternote = afternoteReceiver.getAfternote();
        return ReceivedAfternoteResponse.builder()
                .id(afternote.getId())
                .title(afternote.getTitle())
                .category(afternote.getCategoryType())
                .leaveMessage(afternote.getLeaveMessage())
                .senderId(afternote.getUserId())
                .senderName(senderName)
                .createdAt(afternote.getCreatedAt())
                .build();
    }
}
