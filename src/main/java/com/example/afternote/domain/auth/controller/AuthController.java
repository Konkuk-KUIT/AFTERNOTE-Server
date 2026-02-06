package com.example.afternote.domain.auth.controller;


import com.example.afternote.domain.auth.dto.*;
import com.example.afternote.domain.auth.service.AuthService;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "비밀번호 변경 API", description = "현재 비밀번호와 새 비밀번호를 입력합니다.")
    @PostMapping("/password/change")
    public ApiResponse<Object> passwordChange(
            @Parameter(hidden = true) @UserId Long userId, 
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest
    ) {
        authService.passwordChange(userId, passwordChangeRequest);
        return ApiResponse.success(null);
    }

    @Operation(summary = "로그아웃 API", description = "리프레쉬 토큰을 입력합니다.")
    @PostMapping("/logout")
    public ApiResponse<Object> logout(
            @Parameter(hidden = true) @UserId Long userId,
            @Valid @RequestBody  LogoutRequest logoutRequest
    ) {
        authService.logout(userId, logoutRequest);
        return ApiResponse.success(null);
    }

    @Operation(summary = "이메일 인증번호 전송 API", description = "이메일을 입력해 인증번호를 받습니다.")
    @PostMapping("/email/send")
    public ApiResponse<Object> emailSend(
            @Valid @RequestBody  EmailSendRequest emailSendRequest
    ) {
        authService.emailSend(emailSendRequest);
        return ApiResponse.success(null);
    }

    @Operation(summary = "이메일 인증번호 검증 API", description = "이메일과 인증코드를 통해 검증받습니다.")
    @PostMapping("/email/verify")
    public ApiResponse<Object> emailVerify(
            @Valid @RequestBody   EmailVerifyRequest emailVerifyRequest
    ) {
        authService.emailVerify(emailVerifyRequest);
        return ApiResponse.success(null);
    }

    @Operation(
        summary = "통합 소셜 로그인 API 🎯", 
        description = """
            모든 소셜 로그인 제공자를 통합한 API입니다.
            
            **사용 방법:**
            - provider: KAKAO, GOOGLE, NAVER 등
            - accessToken: 소셜 로그인 제공자로부터 받은 Access Token
            
            **응답:**
            - accessToken: 서비스 JWT Access Token
            - refreshToken: 서비스 JWT Refresh Token
            - isNewUser: 신규 회원 여부 (true/false)
            
            **예시:**
            ```json
            {
              "provider": "KAKAO",
              "accessToken": "ya29.a0AfH6..."
            }
            ```
            """
    )
    @PostMapping("/social/login")
    public ApiResponse<SocialLoginResponse> socialLogin(
            @Valid @RequestBody SocialLoginRequest socialLoginRequest
    ) {
        SocialLoginResponse response = authService.socialLogin(socialLoginRequest);
        return ApiResponse.success(response);
    }





}
