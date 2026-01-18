package com.example.afternote.domain.timeletter.controller;

import com.example.afternote.domain.timeletter.dto.*;
import com.example.afternote.domain.timeletter.service.TimeLetterService;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "📬 TimeLetter API", description = "타임레터 관리 API")
@RestController
@RequestMapping("/time-letters")
@RequiredArgsConstructor
public class TimeLetterController {

    private final TimeLetterService timeLetterService;

    @Operation(summary = "타임레터 전체 조회", description = "정식 등록된(SCHEDULED) 타임레터 전체를 조회합니다.")
    @GetMapping
    public ApiResponse<TimeLetterListResponse> getTimeLetters(
            @Parameter(hidden = true) @UserId Long userId) {
        TimeLetterListResponse response = timeLetterService.getTimeLetters(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "타임레터 단일 조회", description = "특정 타임레터를 조회합니다.")
    @GetMapping("/{timeLetterId}")
    public ApiResponse<TimeLetterResponse> getTimeLetter(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable Long timeLetterId) {
        TimeLetterResponse response = timeLetterService.getTimeLetter(userId, timeLetterId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "타임레터 등록", description = "새 타임레터를 등록합니다. (DRAFT: 임시저장, SCHEDULED: 정식등록)")
    @PostMapping
    public ApiResponse<TimeLetterResponse> createTimeLetter(
            @Parameter(hidden = true) @UserId Long userId,
            @Valid @RequestBody TimeLetterCreateRequest request) {
        TimeLetterResponse response = timeLetterService.createTimeLetter(userId, request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "임시저장 전체 조회", description = "임시저장된(DRAFT) 타임레터 전체를 조회합니다.")
    @GetMapping("/temporary")
    public ApiResponse<TimeLetterListResponse> getTemporaryTimeLetters(
            @Parameter(hidden = true) @UserId Long userId) {
        TimeLetterListResponse response = timeLetterService.getTemporaryTimeLetters(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "타임레터 삭제", description = "타임레터를 단일/다건 삭제합니다.")
    @PostMapping("/delete")
    public ApiResponse<Void> deleteTimeLetters(
            @Parameter(hidden = true) @UserId Long userId,
            @Valid @RequestBody TimeLetterDeleteRequest request) {
        timeLetterService.deleteTimeLetters(userId, request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "임시저장 전체 삭제", description = "임시저장된(DRAFT) 타임레터 전체를 삭제합니다.")
    @DeleteMapping("/temporary")
    public ApiResponse<Void> deleteAllTemporary(
            @Parameter(hidden = true) @UserId Long userId) {
        timeLetterService.deleteAllTemporary(userId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "타임레터 수정", description = "타임레터를 수정합니다.")
    @PatchMapping("/{timeLetterId}")
    public ApiResponse<TimeLetterResponse> updateTimeLetter(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable Long timeLetterId,
            @Valid @RequestBody TimeLetterUpdateRequest request) {
        TimeLetterResponse response = timeLetterService.updateTimeLetter(userId, timeLetterId, request);
        return ApiResponse.success(response);
    }
}
