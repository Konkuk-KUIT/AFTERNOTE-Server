package com.example.afternote.domain.receiver.dto;

import com.example.afternote.domain.receiver.model.TimeLetterReceiver;
import com.example.afternote.domain.timeletter.dto.TimeLetterMediaResponse;
import com.example.afternote.domain.timeletter.model.TimeLetter;
import com.example.afternote.domain.timeletter.model.TimeLetterMedia;
import com.example.afternote.domain.timeletter.model.TimeLetterStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Schema(description = "수신한 타임레터 응답")
@Getter
@Builder
@AllArgsConstructor
public class ReceivedTimeLetterResponse {

    @Schema(description = "타임레터 ID", example = "1")
    private Long id;

    @Schema(description = "수신 타임레터 ID (상세 조회 시 사용)", example = "1")
    private Long timeLetterReceiverId;

    @Schema(description = "제목", example = "미래의 나에게")
    private String title;

    @Schema(description = "내용", example = "1년 후의 나에게 보내는 편지...")
    private String content;

    @Schema(description = "발송 예정 시간")
    private LocalDateTime sendAt;

    @Schema(description = "상태")
    private TimeLetterStatus status;

    @Schema(description = "발신자 이름", example = "김철수")
    private String senderName;

    @Schema(description = "배달 시간")
    private LocalDateTime deliveredAt;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "미디어 목록")
    private List<TimeLetterMediaResponse> mediaList;

    @Schema(description = "읽음 여부")
    private Boolean isRead;

    public static ReceivedTimeLetterResponse from(TimeLetterReceiver timeLetterReceiver, List<TimeLetterMedia> mediaList) {
        TimeLetter timeLetter = timeLetterReceiver.getTimeLetter();
        return ReceivedTimeLetterResponse.builder()
                .id(timeLetter.getId())
                .timeLetterReceiverId(timeLetterReceiver.getId())
                .title(timeLetter.getTitle())
                .content(timeLetter.getContent())
                .sendAt(timeLetter.getSendAt())
                .status(timeLetter.getStatus())
                .senderName(timeLetter.getUser().getName())
                .deliveredAt(timeLetterReceiver.getDeliveredAt())
                .createdAt(timeLetter.getCreatedAt())
                .mediaList(mediaList.stream()
                        .map(TimeLetterMediaResponse::from)
                        .toList())
                .isRead(timeLetterReceiver.getReadAt() != null)
                .build();
    }

    public static ReceivedTimeLetterResponse from(TimeLetterReceiver timeLetterReceiver, List<TimeLetterMedia> mediaList, Function<String, String> urlResolver) {
        TimeLetter timeLetter = timeLetterReceiver.getTimeLetter();
        return ReceivedTimeLetterResponse.builder()
                .id(timeLetter.getId())
                .timeLetterReceiverId(timeLetterReceiver.getId())
                .title(timeLetter.getTitle())
                .content(timeLetter.getContent())
                .sendAt(timeLetter.getSendAt())
                .status(timeLetter.getStatus())
                .senderName(timeLetter.getUser().getName())
                .deliveredAt(timeLetterReceiver.getDeliveredAt())
                .createdAt(timeLetter.getCreatedAt())
                .mediaList(mediaList.stream()
                        .map(m -> TimeLetterMediaResponse.from(m, urlResolver))
                        .toList())
                .isRead(timeLetterReceiver.getReadAt() != null)
                .build();
    }
}
