package com.example.afternote.domain.mindrecord.dto;

import com.example.afternote.domain.mindrecord.image.model.MindRecordImage;
import com.example.afternote.domain.timeletter.model.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Function;

@Schema(description = "마음의 기록 미디어 응답")
@Getter
@Builder
public class MindRecordImageResponse {

    @Schema(description = "미디어 ID", example = "1")
    private Long id;

    @Schema(description = "미디어 타입", example = "IMAGE")
    private MediaType mediaType;

    @Schema(description = "미디어 URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/mindrecords/uuid.jpg")
    private String imageUrl;

    public static MindRecordImageResponse from(MindRecordImage image) {
        return MindRecordImageResponse.builder()
                .id(image.getId())
                .mediaType(image.getMediaType())
                .imageUrl(image.getImageUrl())
                .build();
    }

    public static MindRecordImageResponse from(MindRecordImage image, Function<String, String> urlResolver) {
        return MindRecordImageResponse.builder()
                .id(image.getId())
                .mediaType(image.getMediaType())
                .imageUrl(urlResolver.apply(image.getImageUrl()))
                .build();
    }
}
