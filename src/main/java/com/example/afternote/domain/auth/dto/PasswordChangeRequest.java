package com.example.afternote.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class PasswordChangeRequest {

    @Schema(description = "현재 비밀번호", example = "password123!")
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;


    @Schema(description = "새 비밀번호", example = "password123!")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8~20자의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    private String newPassword;

}
