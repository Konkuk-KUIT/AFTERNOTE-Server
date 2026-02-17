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

@Tag(name = "File API", description = "파일 업로드 관련 API (이미지, 영상, 음성, 문서)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class ImageController {

    private final S3Service s3Service;

    @Operation(summary = "Presigned URL 생성", description = "S3 파일 업로드를 위한 Presigned URL을 생성합니다. 지원 형식: 이미지(jpg, jpeg, png, gif, webp, heic), 영상(mp4, mov), 음성(mp3, m4a, wav), 문서(pdf)")
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
