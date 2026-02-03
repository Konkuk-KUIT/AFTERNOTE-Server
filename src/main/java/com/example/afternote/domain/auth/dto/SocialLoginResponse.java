package com.example.afternote.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 통합 소셜 로그인 응답 DTO
 * 
 * 모든 소셜 로그인 제공자에 대해 동일한 응답 형식을 사용합니다.
 */
@Getter
@AllArgsConstructor
@Builder
public class SocialLoginResponse {
    
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
    @Schema(description = "신규 회원 여부", example = "false")
    private boolean isNewUser;
}
