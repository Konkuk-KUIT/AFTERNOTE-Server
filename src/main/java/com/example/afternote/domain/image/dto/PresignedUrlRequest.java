package com.example.afternote.domain.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {

    @Schema(description = "업로드 디렉토리", example = "profiles",
            allowableValues = {"profiles", "timeletters", "afternotes"})
    @NotBlank(message = "디렉토리는 필수입니다.")
    private String directory;

    @Schema(description = "파일 확장자 (점 없이)", example = "jpg")
    @NotBlank(message = "파일 확장자는 필수입니다.")
    private String extension;
}
