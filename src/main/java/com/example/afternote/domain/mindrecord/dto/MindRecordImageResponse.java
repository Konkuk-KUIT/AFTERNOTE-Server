package com.example.afternote.domain.mindrecord.dto;

import com.example.afternote.domain.mindrecord.image.model.MindRecordImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "마음의 기록 이미지 응답")
@Getter
@Builder
public class MindRecordImageResponse {

    @Schema(description = "이미지 ID", example = "1")
    private Long id;

    @Schema(description = "이미지 URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/mindrecords/uuid.jpg")
    private String imageUrl;

    public static MindRecordImageResponse from(MindRecordImage image) {
        return MindRecordImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .build();
    }
}
