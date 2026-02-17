package com.example.afternote.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserCreateReceiverRequest {

    @Schema(description = "수신자 이름", example = "김지은")
    @NotBlank
    private String name;

    @Schema(description = "사용자와의 관계", example = "DAUGHTER")
    @NotBlank
    private String relation;

    @Schema(description = "전화번호", example = "010-1234-1234", nullable = true)
    private String phone;

    @Schema(description = "이메일", example = "jieun@naver.com", nullable = true)
    private String email;

    @Schema(description = "수신자에게 남길 메시지", example = "사랑하는 딸에게...", nullable = true)
    private String message;
}