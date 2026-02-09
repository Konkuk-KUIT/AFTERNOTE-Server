package com.example.afternote.domain.mindrecord.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "마음의 기록 이미지 요청")
@Getter
@NoArgsConstructor
public class MindRecordImageRequest {

    @Schema(description = "이미지 URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/mindrecords/uuid.jpg")
    @NotBlank(message = "이미지 URL을 입력해주세요.")
    private String imageUrl;
}
