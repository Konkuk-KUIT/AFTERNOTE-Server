package com.example.afternote.domain.user.controller;

import com.example.afternote.domain.user.dto.*;
import com.example.afternote.domain.user.service.UserService;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User API", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "내 프로필 조회 API",
            description = "로그인한 사용자의 프로필 정보를 조회합니다."
    )
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile(
            @UserId Long userId
    ) {
        return ApiResponse.success(
                userService.getMyProfile(userId)
        );
    }

    @Operation(
            summary = "프로필 수정 API",
            description = "로그인한 사용자의 프로필 정보를 수정합니다."
    )
    @PatchMapping("/me")
    public ApiResponse<UserResponse> updateMyProfile(
            @UserId Long userId,
            @Valid @RequestBody UserUpdateProfileRequest request
    ) {
        return ApiResponse.success(
                userService.updateMyProfile(userId, request)
        );
    }

    @Operation(
            summary = "푸시 알림 설정 조회 API",
            description = "로그인한 사용자의 푸시 알림 수신 설정을 불러옵니다."
    )
    @GetMapping("/push-settings")
    public ApiResponse<UserPushSettingResponse> getMyPushSettings(
            @UserId Long userId
    ) {
        return ApiResponse.success(
                userService.getMyPushSettings(userId)
        );
    }

    @Operation(
            summary = "푸시 알림 설정 수정 API",
            description = "로그인한 사용자의 푸시 알림 수신 설정을 수정합니다."
    )
    @PatchMapping("/push-settings")
    public ApiResponse<UserPushSettingResponse> updateMyPushSettings(
            @UserId Long userId,
            @Valid @RequestBody UserUpdatePushSettingRequest request
    ) {
        return ApiResponse.success(
                userService.updateMyPushSettings(userId, request)
        );
    }

    @Operation(
            summary = "수신인 목록 조회 API",
            description = "로그인한 사용자가 등록한 수신인 목록을 조회합니다."
    )
    @GetMapping("/receivers")
    public ApiResponse<List<ReceiverListResponse>> getReceivers(
            @UserId Long userId
    ) {
        return ApiResponse.success(
                userService.getReceivers(userId)
        );
    }

    @Operation(
            summary = "수신자 등록 API",
            description = "로그인한 사용자가 새로운 수신자를 등록합니다."
    )
    @PostMapping("/receivers")
    public ApiResponse<UserCreateReceiverResponse> createReceiver(
            @UserId Long userId,
            @Valid @RequestBody UserCreateReceiverRequest request
    ) {
        return ApiResponse.success(
                userService.createReceiver(userId, request)
        );
    }

    @Operation(
            summary = "수신인 상세 조회 API",
            description = "특정 수신인의 상세 정보를 조회합니다."
    )
    @GetMapping("/receivers/{receiverId}")
    public ApiResponse<ReceiverDetailResponse> getReceiverDetail(
            @UserId Long userId,
            @PathVariable Long receiverId
    ) {
        return ApiResponse.success(
                userService.getReceiverDetail(userId, receiverId)
        );
    }
}
