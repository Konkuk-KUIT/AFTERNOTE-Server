package com.example.afternote.domain.mindrecord.dto;

import com.example.afternote.domain.timeletter.model.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "마음의 기록 미디어 요청")
@Getter
@NoArgsConstructor
public class MindRecordImageRequest {

    @Schema(description = "미디어 타입 (미입력 시 IMAGE)", example = "IMAGE",
            allowableValues = {"IMAGE", "VIDEO", "AUDIO", "DOCUMENT"})
    private MediaType mediaType;

    @Schema(description = "미디어 URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/mindrecords/uuid.jpg")
    @NotBlank(message = "미디어 URL을 입력해주세요.")
    private String imageUrl;
}
