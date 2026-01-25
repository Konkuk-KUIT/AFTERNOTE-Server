package com.example.afternote.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateProfileRequest {
    @Schema(description = "사용자 이름", example = "김소희", nullable = true)
    private String name;

    @Schema(description = "연락처", example = "01012345678", nullable = true)
    private String phone;

    @Schema(description = "프로필 이미지 URL",
            example = "https://cdn.example.com/profile/1.png", nullable = true)
    private String profileImageUrl;
}
