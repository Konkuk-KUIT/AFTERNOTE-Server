package com.example.afternote.domain.receiver.controller;

import com.example.afternote.domain.receiver.dto.ReceivedAfternoteListResponse;
import com.example.afternote.domain.receiver.dto.ReceivedMindRecordListResponse;
import com.example.afternote.domain.receiver.dto.ReceivedTimeLetterListResponse;
import com.example.afternote.domain.receiver.dto.ReceiverAuthVerifyRequest;
import com.example.afternote.domain.receiver.dto.ReceiverAuthVerifyResponse;
import com.example.afternote.domain.receiver.service.ReceiverAuthService;
import com.example.afternote.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Receiver Auth API", description = "수신자 인증번호 기반 콘텐츠 조회 API")
@RestController
@RequestMapping("/api/receiver-auth")
@RequiredArgsConstructor
public class ReceiverAuthController {

    private final ReceiverAuthService receiverAuthService;

    @Operation(
            summary = "인증번호 검증",
            description = "수신자 인증번호를 검증하고 수신자/발신자 정보를 반환합니다."
    )
    @PostMapping("/verify")
    public ApiResponse<ReceiverAuthVerifyResponse> verifyAuthCode(
            @Valid @RequestBody ReceiverAuthVerifyRequest request
    ) {
        return ApiResponse.success(receiverAuthService.verifyAuthCode(request.getAuthCode()));
    }

    @Operation(
            summary = "인증번호로 타임레터 목록 조회",
            description = "인증번호를 통해 수신자에게 배달된 타임레터 목록을 조회합니다."
    )
    @GetMapping("/{authCode}/time-letters")
    public ApiResponse<ReceivedTimeLetterListResponse> getTimeLetters(
            @Parameter(description = "수신자 인증번호 (UUID)")
            @PathVariable String authCode
    ) {
        return ApiResponse.success(receiverAuthService.getTimeLettersByAuthCode(authCode));
    }

    @Operation(
            summary = "인증번호로 애프터노트 목록 조회",
            description = "인증번호를 통해 수신자에게 전달된 애프터노트 목록을 조회합니다."
    )
    @GetMapping("/{authCode}/after-notes")
    public ApiResponse<ReceivedAfternoteListResponse> getAfternotes(
            @Parameter(description = "수신자 인증번호 (UUID)")
            @PathVariable String authCode
    ) {
        return ApiResponse.success(receiverAuthService.getAfternotesByAuthCode(authCode));
    }

    @Operation(
            summary = "인증번호로 마인드레코드 목록 조회",
            description = "인증번호를 통해 수신자에게 공유된 마인드레코드 목록을 조회합니다."
    )
    @GetMapping("/{authCode}/mind-records")
    public ApiResponse<ReceivedMindRecordListResponse> getMindRecords(
            @Parameter(description = "수신자 인증번호 (UUID)")
            @PathVariable String authCode
    ) {
        return ApiResponse.success(receiverAuthService.getMindRecordsByAuthCode(authCode));
    }
}
