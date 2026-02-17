package com.example.afternote.domain.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PresignedUrlResponse {

    @Schema(description = "S3 Presigned PUT URL (파일 업로드용)",
            example = "https://afternote-bucket.s3.ap-northeast-2.amazonaws.com/profiles/...")
    private String presignedUrl;

    @Schema(description = "업로드 완료 후 사용할 파일 URL",
            example = "https://afternote-bucket.s3.ap-northeast-2.amazonaws.com/profiles/uuid.jpg")
    private String fileUrl;

    @Schema(description = "PUT 요청 시 사용할 Content-Type 헤더 값",
            example = "image/jpeg")
    private String contentType;
}
