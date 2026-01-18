package com.example.afternote.domain.timeletter.dto;

import com.example.afternote.domain.timeletter.model.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "타임레터 미디어 요청")
@Getter
@NoArgsConstructor
public class TimeLetterMediaRequest {

    @Schema(description = "미디어 타입", example = "IMAGE")
    @NotNull(message = "미디어 타입을 입력해주세요.")
    private MediaType mediaType;

    @Schema(description = "미디어 URL", example = "https://example.com/image.jpg")
    @NotBlank(message = "미디어 URL을 입력해주세요.")
    private String mediaUrl;
}
