package com.example.afternote.domain.receiver.controller;

import com.example.afternote.domain.receiver.dto.ReceivedAfternoteListResponse;
import com.example.afternote.domain.receiver.dto.DeliveryVerificationRequest;
import com.example.afternote.domain.receiver.dto.DeliveryVerificationResponse;
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
    @GetMapping("/time-letters")
    public ApiResponse<ReceivedTimeLetterListResponse> getTimeLetters(
            @Parameter(description = "수신자 인증번호 (UUID)", required = true)
            @RequestHeader("X-Auth-Code") String authCode
    ) {
        return ApiResponse.success(receiverAuthService.getTimeLettersByAuthCode(authCode));
    }

    @Operation(
            summary = "인증번호로 애프터노트 목록 조회",
            description = "인증번호를 통해 수신자에게 전달된 애프터노트 목록을 조회합니다."
    )
    @GetMapping("/after-notes")
    public ApiResponse<ReceivedAfternoteListResponse> getAfternotes(
            @Parameter(description = "수신자 인증번호 (UUID)", required = true)
            @RequestHeader("X-Auth-Code") String authCode
    ) {
        return ApiResponse.success(receiverAuthService.getAfternotesByAuthCode(authCode));
    }

    @Operation(
            summary = "인증번호로 마인드레코드 목록 조회",
            description = "인증번호를 통해 수신자에게 공유된 마인드레코드 목록을 조회합니다."
    )
    @GetMapping("/mind-records")
    public ApiResponse<ReceivedMindRecordListResponse> getMindRecords(
            @Parameter(description = "수신자 인증번호 (UUID)", required = true)
            @RequestHeader("X-Auth-Code") String authCode
    ) {
        return ApiResponse.success(receiverAuthService.getMindRecordsByAuthCode(authCode));
    }

    @Operation(
            summary = "사망확인 서류 제출",
            description = "전달 조건이 DEATH_CERTIFICATE인 경우 수신자가 인증 서류를 제출합니다."
    )
    @PostMapping("/delivery-verification")
    public ApiResponse<DeliveryVerificationResponse> submitDeliveryVerification(
            @Parameter(description = "수신자 인증번호 (UUID)", required = true)
            @RequestHeader("X-Auth-Code") String authCode,
            @Valid @RequestBody DeliveryVerificationRequest request
    ) {
        return ApiResponse.success(receiverAuthService.submitDeliveryVerification(authCode, request));
    }

    @Operation(
            summary = "사망확인 인증 상태 조회",
            description = "수신자가 마지막으로 제출한 인증 요청 상태를 조회합니다."
    )
    @GetMapping("/delivery-verification/status")
    public ApiResponse<DeliveryVerificationResponse> getDeliveryVerificationStatus(
            @Parameter(description = "수신자 인증번호 (UUID)", required = true)
            @RequestHeader("X-Auth-Code") String authCode
    ) {
        return ApiResponse.success(receiverAuthService.getDeliveryVerificationStatus(authCode));
    }
}
