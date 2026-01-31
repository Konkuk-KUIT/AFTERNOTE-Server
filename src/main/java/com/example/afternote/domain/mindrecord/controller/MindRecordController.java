package com.example.afternote.domain.mindrecord.controller;

import com.example.afternote.domain.mindrecord.dto.GetMindRecordListRequest;
import com.example.afternote.domain.mindrecord.dto.GetMindRecordListResponse;
import com.example.afternote.domain.mindrecord.service.MindRecordService;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
            @Parameter(hidden = true) @UserId Long userId,
            GetMindRecordListRequest request
    ) {
        return ApiResponse.success(
                mindRecordService.getMindRecordList(userId, request)
        );
    }
}