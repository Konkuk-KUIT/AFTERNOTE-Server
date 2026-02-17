package com.example.afternote.domain.admin.controller;

import com.example.afternote.domain.admin.dto.AdminVerificationActionRequest;
import com.example.afternote.domain.admin.dto.AdminVerificationResponse;
import com.example.afternote.domain.admin.service.AdminService;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin API", description = "관리자용 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "대기 중인 인증 요청 목록 조회",
            description = """
                    관리자가 대기 중인(PENDING) 사망확인 인증 요청 목록을 조회합니다.

                    **관리자 권한 필요** - ADMIN 역할이 아닌 경우 403 에러가 반환됩니다.

                    **응답에 포함되는 정보:**
                    - 발신자/수신자 정보 (이름, 이메일)
                    - 사망진단서 및 가족관계증명서 Presigned URL (1시간 후 만료)
                    - 제출 일시, 관리자 메모
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "대기 중인 인증 요청 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 요청 (code: 999)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한 필요 (code: 605)")
    })
    @GetMapping("/verifications")
    public ApiResponse<List<AdminVerificationResponse>> getPendingVerifications(
            @Parameter(hidden = true) @UserId Long userId
    ) {
        return ApiResponse.success(adminService.getPendingVerifications(userId));
    }

    @Operation(
            summary = "인증 요청 상세 조회",
            description = """
                    관리자가 특정 인증 요청의 상세 정보를 조회합니다.

                    **관리자 권한 필요**

                    Presigned URL이 포함되어 있으므로, URL 만료 전에 서류를 확인하세요.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 요청 상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 요청 (code: 999)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한 필요 (code: 605)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "인증 요청을 찾을 수 없음 (code: 602)")
    })
    @GetMapping("/verifications/{id}")
    public ApiResponse<AdminVerificationResponse> getVerificationDetail(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable Long id
    ) {
        return ApiResponse.success(adminService.getVerificationDetail(userId, id));
    }

    @Operation(
            summary = "인증 요청 승인",
            description = """
                    관리자가 사망확인 인증 요청을 승인합니다.

                    **승인 시 동작:**
                    - 인증 상태가 `APPROVED`로 변경됩니다.
                    - 발신자의 `conditionFulfilled`가 `true`로 변경되어 수신자가 콘텐츠를 열람할 수 있게 됩니다.
                    - `adminNote`(선택)를 통해 메모를 남길 수 있습니다.

                    **주의:** 이미 처리된(APPROVED/REJECTED) 요청은 재처리할 수 없습니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 요청 승인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 처리된 인증 요청 (code: 604)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 요청 (code: 999)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한 필요 (code: 605)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "인증 요청을 찾을 수 없음 (code: 602)")
    })
    @PostMapping("/verifications/{id}/approve")
    public ApiResponse<AdminVerificationResponse> approveVerification(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable Long id,
            @RequestBody(required = false) AdminVerificationActionRequest request
    ) {
        String adminNote = request != null ? request.getAdminNote() : null;
        return ApiResponse.success(adminService.approveVerification(userId, id, adminNote));
    }

    @Operation(
            summary = "인증 요청 거절",
            description = """
                    관리자가 사망확인 인증 요청을 거절합니다.

                    **거절 시 동작:**
                    - 인증 상태가 `REJECTED`로 변경됩니다.
                    - 수신자는 서류를 다시 제출할 수 있습니다.
                    - `adminNote`(선택)를 통해 거절 사유를 남길 수 있습니다.

                    **주의:** 이미 처리된(APPROVED/REJECTED) 요청은 재처리할 수 없습니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 요청 거절 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 처리된 인증 요청 (code: 604)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 요청 (code: 999)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한 필요 (code: 605)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "인증 요청을 찾을 수 없음 (code: 602)")
    })
    @PostMapping("/verifications/{id}/reject")
    public ApiResponse<AdminVerificationResponse> rejectVerification(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable Long id,
            @RequestBody(required = false) AdminVerificationActionRequest request
    ) {
        String adminNote = request != null ? request.getAdminNote() : null;
        return ApiResponse.success(adminService.rejectVerification(userId, id, adminNote));
    }
}
