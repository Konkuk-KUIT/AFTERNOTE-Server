package com.example.afternote.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum WhiteListUrl {

    //swagger
    SWAGGER_UI("/swagger-ui/**"),
    API_DOCS("/v3/api-docs/**"),
    RESOURCES("/swagger-resources/**"),

    //회원가입, 로그인 (reissue는 JWT 검증 필요하므로 제외)
    AUTH_SIGNUP("/auth/sign-up"),
    AUTH_LOGIN("/auth/login"),
    AUTH_REISSUE("/auth/reissue"),
    AUTH_LOGOUT("/auth/logout");


    private final String url;

    public static String[] getAllUrls() {
        return Arrays.stream(values())
                .map(WhiteListUrl::getUrl)
                .toArray(String[]::new);
    }
}