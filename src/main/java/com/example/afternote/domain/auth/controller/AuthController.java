package com.example.afternote.domain.auth.controller;


import com.example.afternote.domain.auth.dto.*;
import com.example.afternote.domain.auth.service.AuthService;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "🔐 auth API", description = "회원가입, 로그인 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;


    @Operation(summary = "회원가입 API", description = "회원가입을 합니다.")
    @PostMapping("/sign-up")
    public ApiResponse<SignupResponse> signUp(@Valid @RequestBody SignupRequest signupRequest) {
        // 회원가입 로직 구현
        User newuser = authService.signup(signupRequest);
        return ApiResponse.success(SignupResponse.from(newuser));
    }

    @Operation(summary = "로그인 API", description = "로그인을 합니다.")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 로그인 로직 구현
        LoginResponse loginResponse = authService.login(loginRequest);
        return ApiResponse.success(loginResponse);
    }

    @Operation(summary = "토큰 재발급 API", description = "리프레쉬 토큰을 이용해 다시 재발급을 합니다.")
    @PostMapping("/reissue")
    public ApiResponse<ReissueResponse> reissue(@Valid @RequestBody ReissueRequest reissueRequest) {

        ReissueResponse reissueResponse = authService.reissue(reissueRequest);
        return ApiResponse.success(reissueResponse);
    }
}
