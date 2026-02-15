package com.example.afternote.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum WhiteListUrl {

    //swaggar
    SWAGGER_UI("/swagger-ui/**"),
    API_DOCS("/v3/api-docs/**"),
    RESOURCES("/swagger-resources/**"),

    //회원가입, 로그인, 토큰 재발급
    AUTH_SIGNUP("/auth/sign-up"),
    AUTH_LOGIN("/auth/login"),
    AUTH_REISSUE("/auth/reissue"),

    // 수신자 인증
    RECEIVER_AUTH("/api/receiver-auth/**");

    private final String url;

    public static String[] getAllUrls() {
        return Arrays.stream(values())
                .map(WhiteListUrl::getUrl)
                .toArray(String[]::new);
    }
}