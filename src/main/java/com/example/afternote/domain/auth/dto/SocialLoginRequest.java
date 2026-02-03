package com.example.afternote.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 통합 소셜 로그인 요청 DTO
 * 
 * 모든 소셜 로그인 제공자에 대해 동일한 요청 형식을 사용합니다.
 */
@Getter
@NoArgsConstructor
public class SocialLoginRequest {
    
    @Schema(description = "소셜 로그인 제공자 (KAKAO, GOOGLE, NAVER 등)", example = "KAKAO")
    @NotBlank(message = "소셜 로그인 제공자를 입력해주세요.")
    private String provider;
    
    @Schema(description = "소셜 로그인 Access Token", example = "ya29.a0AfH6...")
    @NotBlank(message = "Access Token을 입력해주세요.")
    private String accessToken;
}
