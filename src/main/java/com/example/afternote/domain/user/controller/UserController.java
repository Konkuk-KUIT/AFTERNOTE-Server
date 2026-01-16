package com.example.afternote.domain.user.controller;

import com.example.afternote.domain.user.dto.UserResponse;
import com.example.afternote.domain.user.service.UserService;
import com.example.afternote.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "내 프로필 조회 API",
            description = "로그인한 사용자의 프로필 정보를 조회합니다."
    )
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(
                userService.getMyProfile(userId)
        );
    }
}
