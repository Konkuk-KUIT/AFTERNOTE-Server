package com.example.afternote.domain.user.model;

/**
 * 소셜 로그인 제공자 타입
 * 
 * 새로운 소셜 로그인을 추가할 때 여기에 추가하면 됩니다.
 */
public enum AuthProvider {
    LOCAL,   // 일반 회원가입
    KAKAO,   // 카카오 로그인
    GOOGLE,  // 구글 로그인
    NAVER    // 네이버 로그인 (향후 추가 예정)
}
