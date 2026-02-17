package com.example.afternote.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserConnectedAccountResponse {
    @Schema(description = "일반(로컬) 계정 연결 여부", example = "true")
    private boolean local;

    @Schema(description = "구글 계정 연결 여부", example = "false")
    private boolean google;

    @Schema(description = "네이버 계정 연결 여부", example = "false")
    private boolean naver;

    @Schema(description = "카카오 계정 연결 여부", example = "true")
    private boolean kakao;

    @Schema(description = "애플 계정 연결 여부", example = "false")
    private boolean apple;
}
