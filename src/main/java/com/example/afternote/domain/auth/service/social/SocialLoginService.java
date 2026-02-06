package com.example.afternote.domain.auth.service.social;

import com.example.afternote.domain.auth.dto.SocialUserInfo;

/**
 * 소셜 로그인 전략 패턴의 핵심 인터페이스
 * 
 * OCP 원칙을 준수하여 새로운 소셜 로그인 제공자를 추가할 때
 * 기존 코드 수정 없이 이 인터페이스만 구현하면 됩니다.
 * 
 * @author AfterNote Team
 */
public interface SocialLoginService {
    
    /**
     * 소셜 로그인 제공자의 Access Token으로 사용자 정보를 조회
     * 
     * @param accessToken 소셜 로그인 제공자로부터 받은 Access Token
     * @return 소셜 로그인 사용자 정보
     */
    SocialUserInfo getUserInfo(String accessToken);
    
    /**
     * 어떤 소셜 로그인 제공자인지 반환
     * 
     * @return 제공자 타입 (KAKAO, GOOGLE, NAVER 등)
     */
    String getProviderName();
}
