package com.example.afternote.domain.image.controller;

import com.example.afternote.domain.image.dto.PresignedUrlRequest;
import com.example.afternote.domain.image.dto.PresignedUrlResponse;
import com.example.afternote.domain.image.service.S3Service;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Image API", description = "이미지 업로드 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final S3Service s3Service;

    @Operation(summary = "Presigned URL 생성", description = "S3 이미지 업로드를 위한 Presigned URL을 생성합니다.")
    @PostMapping("/presigned-url")
    public ApiResponse<PresignedUrlResponse> getPresignedUrl(
            @Parameter(hidden = true) @UserId Long userId,
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        return ApiResponse.success(
                s3Service.generatePresignedUrl(request.getDirectory(), request.getExtension())
        );
    }
}
