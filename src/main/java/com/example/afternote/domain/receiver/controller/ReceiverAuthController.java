package com.example.afternote.domain.receiver.controller;

import com.example.afternote.domain.receiver.dto.*;
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
            summary = "인증번호로 타임레터 상세 조회",
            description = "인증번호를 통해 수신한 특정 타임레터를 상세 조회합니다. 읽음 처리도 함께 수행됩니다."
    )
    @GetMapping("/time-letters/{timeLetterReceiverId}")
    public ApiResponse<ReceivedTimeLetterResponse> getTimeLetter(
            @Parameter(description = "수신자 인증번호 (UUID)", required = true)
            @RequestHeader("X-Auth-Code") String authCode,
            @Parameter(description = "수신 타임레터 ID", required = true)
            @PathVariable Long timeLetterReceiverId
    ) {
        return ApiResponse.success(receiverAuthService.getTimeLetterByAuthCode(authCode, timeLetterReceiverId));
    }

    @Operation(
            summary = "인증번호로 마인드레코드 상세 조회",
            description = "인증번호를 통해 수신한 특정 마인드레코드의 상세 내용을 조회합니다."
    )
    @GetMapping("/mind-records/{mindRecordId}")
    public ApiResponse<ReceivedMindRecordDetailResponse> getMindRecord(
            @Parameter(description = "수신자 인증번호 (UUID)", required = true)
            @RequestHeader("X-Auth-Code") String authCode,
            @Parameter(description = "마인드레코드 ID", required = true)
            @PathVariable Long mindRecordId
    ) {
        return ApiResponse.success(receiverAuthService.getMindRecordByAuthCode(authCode, mindRecordId));
    }

    @Operation(
            summary = "인증번호로 애프터노트 상세 조회",
            description = "인증번호를 통해 수신한 특정 애프터노트의 상세 내용을 조회합니다."
    )
    @GetMapping("/after-notes/{afternoteId}")
    public ApiResponse<ReceivedAfternoteDetailResponse> getAfternote(
            @Parameter(description = "수신자 인증번호 (UUID)", required = true)
            @RequestHeader("X-Auth-Code") String authCode,
            @Parameter(description = "애프터노트 ID", required = true)
            @PathVariable Long afternoteId
    ) {
        return ApiResponse.success(receiverAuthService.getAfternoteByAuthCode(authCode, afternoteId));
    }

    @Operation(
            summary = "발신자 메시지 조회",
            description = "인증번호를 통해 발신자가 남긴 메시지를 조회합니다."
    )
    @GetMapping("/message")
    public ApiResponse<ReceiverMessageResponse> getMessage(
            @Parameter(description = "수신자 인증번호 (UUID)", required = true)
            @RequestHeader("X-Auth-Code") String authCode
    ) {
        return ApiResponse.success(receiverAuthService.getMessageByAuthCode(authCode));
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
