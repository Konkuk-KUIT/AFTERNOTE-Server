package com.example.afternote.domain.receiver.dto;

import com.example.afternote.domain.mindrecord.diary.model.Diary;
import com.example.afternote.domain.mindrecord.dto.MindRecordImageResponse;
import com.example.afternote.domain.mindrecord.image.model.MindRecordImage;
import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.model.MindRecordType;
import com.example.afternote.domain.mindrecord.question.model.DailyQuestionAnswer;
import com.example.afternote.domain.mindrecord.thought.model.DeepThought;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Schema(description = "수신한 마인드레코드 상세 응답")
@Getter
@Builder
@AllArgsConstructor
public class ReceivedMindRecordDetailResponse {

    @Schema(description = "마인드레코드 ID", example = "1")
    private Long id;

    @Schema(description = "기록 타입", example = "DIARY")
    private MindRecordType type;

    @Schema(description = "제목", example = "오늘의 일기")
    private String title;

    @Schema(description = "기록 날짜")
    private LocalDate recordDate;

    @Schema(description = "본문 내용")
    private String content;

    @Schema(description = "데일리 질문 ID (DAILY_QUESTION 타입일 때)", nullable = true)
    private Long questionId;

    @Schema(description = "데일리 질문 내용 (DAILY_QUESTION 타입일 때)", nullable = true)
    private String questionContent;

    @Schema(description = "깊은 생각 카테고리 (DEEP_THOUGHT 타입일 때)", nullable = true)
    private String category;

    @Schema(description = "발신자 이름", example = "김철수")
    private String senderName;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "이미지 목록")
    private List<MindRecordImageResponse> imageList;

    public static ReceivedMindRecordDetailResponse from(
            MindRecord record, Diary diary,
            List<MindRecordImage> images, Function<String, String> urlResolver) {
        return base(record)
                .content(diary.getContent())
                .imageList(toImageResponses(images, urlResolver))
                .build();
    }

    public static ReceivedMindRecordDetailResponse from(
            MindRecord record, DailyQuestionAnswer answer,
            List<MindRecordImage> images, Function<String, String> urlResolver) {
        return base(record)
                .content(answer.getContent())
                .questionId(answer.getDailyQuestion().getId())
                .questionContent(answer.getDailyQuestion().getContent())
                .imageList(toImageResponses(images, urlResolver))
                .build();
    }

    public static ReceivedMindRecordDetailResponse from(
            MindRecord record, DeepThought thought,
            List<MindRecordImage> images, Function<String, String> urlResolver) {
        return base(record)
                .content(thought.getContent())
                .category(thought.getCategory())
                .imageList(toImageResponses(images, urlResolver))
                .build();
    }

    private static ReceivedMindRecordDetailResponseBuilder base(MindRecord record) {
        return ReceivedMindRecordDetailResponse.builder()
                .id(record.getId())
                .type(record.getType())
                .title(record.getTitle())
                .recordDate(record.getRecordDate())
                .senderName(record.getUser().getName())
                .createdAt(record.getCreatedAt());
    }

    private static List<MindRecordImageResponse> toImageResponses(
            List<MindRecordImage> images, Function<String, String> urlResolver) {
        return images.stream()
                .map(img -> MindRecordImageResponse.from(img, urlResolver))
                .toList();
    }
}
