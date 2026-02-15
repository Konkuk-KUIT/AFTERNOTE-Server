package com.example.afternote.domain.user.dto;

import com.example.afternote.domain.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Function;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    @Schema(description = "사용자 이름", example = "김소희")
    private String name;

    @Schema(description = "이메일", example = "afternote@example.com")
    private String email;

    @Schema(description = "연락처", example = "01012345678", nullable = true)
    private String phone;

    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profile/1.png", nullable = true)
    private String profileImageUrl;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public static UserResponse from(User user, Function<String, String> urlResolver) {
        String profileImageUrl = user.getProfileImageUrl();
        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImageUrl(profileImageUrl != null ? urlResolver.apply(profileImageUrl) : null)
                .build();
    }
}