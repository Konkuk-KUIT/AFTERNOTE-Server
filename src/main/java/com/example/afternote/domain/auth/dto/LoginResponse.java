package com.example.afternote.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {

    @Schema(description = "리프레쉬 토큰" ,example = "safasfskldfjasdkgwr34tklgfdldfsdckl...")
    private String refreshToken;

    @Schema(description = "액세스 토큰" ,example = "safasfskldfjasdkgwr34tklgfdldfsdckl...")
    private String accessToken;


}
