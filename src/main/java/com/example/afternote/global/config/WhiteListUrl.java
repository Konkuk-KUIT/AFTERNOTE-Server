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

    //회원가입
    AUTH("/auth/**");

    private final String url;

    public static String[] getAllUrls() {
        return Arrays.stream(values())
                .map(WhiteListUrl::getUrl)
                .toArray(String[]::new);
    }
}