package com.example.afternote.domain.mindrecord.controller;

import com.example.afternote.domain.mindrecord.dto.*;
import com.example.afternote.domain.mindrecord.service.MindRecordService;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MindRecord API", description = "마음의 기록 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mind-records")
public class MindRecordController {

    private final MindRecordService mindRecordService;

    @Operation(
            summary = "마음의 기록 목록 조회 API",
            description = " 마음의 기록을 조회합니다."
    )
    @GetMapping
    public ApiResponse<GetMindRecordListResponse> getMindRecordList(
            @UserId Long userId,
            GetMindRecordListRequest request
    ) {
        return ApiResponse.success(
                mindRecordService.getMindRecordList(userId, request)
        );
    }

    @Operation(
            summary = "마음의 기록 작성 API",
            description = "마음의 기록을 작성합니다."
    )
    @PostMapping
    public ApiResponse<PostMindRecordResponse> createMindRecord(
            @UserId Long userId,
            @Valid @RequestBody PostMindRecordRequest request
    ) {
        Long recordId = mindRecordService.createMindRecord(userId, request);
        return ApiResponse.success(new PostMindRecordResponse(recordId));
    }

    /**
     * 마음의 기록 단건 수정 화면 조회
     */
    @Operation(
            summary = "마음의 기록 단건 조회 (수정 화면) API",
            description = "기록 수정 화면 진입 시 사용하는 단건 조회 API"
    )
    @GetMapping("/{recordId}")
    public ApiResponse<GetMindRecordDetailResponse> getMindRecordDetail(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable Long recordId
    ) {
        return ApiResponse.success(
                mindRecordService.getMindRecordDetail(userId, recordId)
        );
    }

    @DeleteMapping("/{recordId}")
    @Operation(
            summary = "마음의 기록 삭제 API",
            description = "특정 마음의 기록을 삭제합니다."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMindRecord(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable Long recordId
    ) {
        mindRecordService.deleteMindRecord(userId, recordId);
    }
}