package com.example.afternote.domain.auth.dto;

import com.example.afternote.domain.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class SignupResponse {

    @Schema(description = "가입된 유저 ID", example = "1")
    private Long userId;

    @Schema(description = "가입된 이메일", example = "user@example.com")
    private String email;

    public static SignupResponse from(User user) {
        return SignupResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

}
