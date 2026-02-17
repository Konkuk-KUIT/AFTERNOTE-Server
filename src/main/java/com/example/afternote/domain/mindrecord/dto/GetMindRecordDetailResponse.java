package com.example.afternote.domain.mindrecord.dto;

import com.example.afternote.domain.mindrecord.diary.model.Diary;
import com.example.afternote.domain.mindrecord.image.model.MindRecordImage;
import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.model.MindRecordType;
import com.example.afternote.domain.mindrecord.question.model.DailyQuestionAnswer;
import com.example.afternote.domain.mindrecord.thought.model.DeepThought;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
@Builder
@AllArgsConstructor
public class GetMindRecordDetailResponse {

    @Schema(description = "마음의 기록 ID", example = "3")
    private Long recordId;

    @Schema(description = "기록 타입", example = "DIARY")
    private MindRecordType type;

    @Schema(description = "제목", example = "오늘의 일기")
    private String title;

    @Schema(description = "기록 날짜", example = "2026-01-27")
    private String date;

    @Schema(description = "임시저장 여부", example = "false")
    private Boolean isDraft;

    @Schema(description = "본문 내용 (일기 / 깊은 생각 / 데일리 질문 답변)", nullable = true)
    private String content;

    @Schema(description = "데일리 질문 ID (DAILY_QUESTION 타입일 때)", nullable = true, example = "12")
    private Long questionId;

    @Schema(description = "깊은 생각 카테고리 (DEEP_THOUGHT 타입일 때)", nullable = true, example = "자아성찰")
    private String category;

    @Schema(description = "미디어 목록")
    private List<MindRecordImageResponse> imageList;

    public static GetMindRecordDetailResponse from(MindRecord record, Diary diary, List<MindRecordImage> images) {
        return base(record)
                .content(diary.getContent())
                .imageList(images.stream().map(MindRecordImageResponse::from).toList())
                .build();
    }

    public static GetMindRecordDetailResponse from(MindRecord record, DailyQuestionAnswer answer, List<MindRecordImage> images) {
        return base(record)
                .content(answer.getContent())
                .questionId(answer.getDailyQuestion().getId())
                .imageList(images.stream().map(MindRecordImageResponse::from).toList())
                .build();
    }

    public static GetMindRecordDetailResponse from(MindRecord record, DeepThought thought, List<MindRecordImage> images) {
        return base(record)
                .content(thought.getContent())
                .category(thought.getCategory())
                .imageList(images.stream().map(MindRecordImageResponse::from).toList())
                .build();
    }

    public static GetMindRecordDetailResponse from(MindRecord record, Diary diary, List<MindRecordImage> images, Function<String, String> urlResolver) {
        return base(record)
                .content(diary.getContent())
                .imageList(images.stream().map(img -> MindRecordImageResponse.from(img, urlResolver)).toList())
                .build();
    }

    public static GetMindRecordDetailResponse from(MindRecord record, DailyQuestionAnswer answer, List<MindRecordImage> images, Function<String, String> urlResolver) {
        return base(record)
                .content(answer.getContent())
                .questionId(answer.getDailyQuestion().getId())
                .imageList(images.stream().map(img -> MindRecordImageResponse.from(img, urlResolver)).toList())
                .build();
    }

    public static GetMindRecordDetailResponse from(MindRecord record, DeepThought thought, List<MindRecordImage> images, Function<String, String> urlResolver) {
        return base(record)
                .content(thought.getContent())
                .category(thought.getCategory())
                .imageList(images.stream().map(img -> MindRecordImageResponse.from(img, urlResolver)).toList())
                .build();
    }

    private static GetMindRecordDetailResponseBuilder base(MindRecord record) {
        return GetMindRecordDetailResponse.builder()
                .recordId(record.getId())
                .type(record.getType())
                .title(record.getTitle())
                .date(record.getRecordDate().toString())
                .isDraft(record.getIsDraft());
    }
}