package com.example.afternote.domain.receiver.controller;

import com.example.afternote.domain.receiver.dto.ReceivedAfternoteListResponse;
import com.example.afternote.domain.receiver.dto.ReceivedMindRecordListResponse;
import com.example.afternote.domain.receiver.dto.ReceivedTimeLetterListResponse;
import com.example.afternote.domain.receiver.service.ReceivedService;
import com.example.afternote.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Received API", description = "수신자용 콘텐츠 조회 API")
@RestController
@RequestMapping("/api/received")
@RequiredArgsConstructor
public class ReceivedController {

    private final ReceivedService receivedService;

    @Operation(
            summary = "수신한 타임레터 목록 조회",
            description = "수신자에게 배달된 타임레터 목록을 조회합니다."
    )
    @GetMapping("/{receiverId}/time-letters")
    public ApiResponse<ReceivedTimeLetterListResponse> getTimeLetters(
            @Parameter(description = "수신자 ID", example = "1")
            @PathVariable Long receiverId
    ) {
        return ApiResponse.success(receivedService.getTimeLetters(receiverId));
    }

    @Operation(
            summary = "수신한 애프터노트 목록 조회",
            description = "수신자에게 전달된 애프터노트 목록을 조회합니다."
    )
    @GetMapping("/{receiverId}/after-notes")
    public ApiResponse<ReceivedAfternoteListResponse> getAfternotes(
            @Parameter(description = "수신자 ID", example = "1")
            @PathVariable Long receiverId
    ) {
        return ApiResponse.success(receivedService.getAfternotes(receiverId));
    }

    @Operation(
            summary = "수신한 마인드레코드 목록 조회",
            description = "수신자에게 공유된 마인드레코드(일기, 질문답변, 깊은생각) 목록을 조회합니다."
    )
    @GetMapping("/{receiverId}/mind-records")
    public ApiResponse<ReceivedMindRecordListResponse> getMindRecords(
            @Parameter(description = "수신자 ID", example = "1")
            @PathVariable Long receiverId
    ) {
        return ApiResponse.success(receivedService.getMindRecords(receiverId));
    }
}
