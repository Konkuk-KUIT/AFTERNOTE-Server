package com.example.afternote.domain.auth.dto;

import com.example.afternote.domain.user.model.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소셜 로그인으로부터 받아온 사용자 정보를 담는 공통 DTO
 * 
 * 각 소셜 로그인 제공자(카카오, 구글, 네이버 등)의 응답을
 * 이 공통 형식으로 변환하여 사용합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialUserInfo {
    
    /**
     * 소셜 로그인 제공자로부터 받은 고유 ID
     * 예: 카카오의 경우 user_id, 구글의 경우 sub
     */
    private String providerId;
    
    /**
     * 사용자 이메일
     */
    private String email;
    
    /**
     * 사용자 이름
     */
    private String name;
    
    /**
     * 소셜 로그인 제공자 타입
     * KAKAO, GOOGLE, NAVER 등
     */
    private AuthProvider provider;
    
    /**
     * 프로필 이미지 URL (선택적)
     */
    private String profileImageUrl;
}
