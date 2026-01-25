package com.example.afternote.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserUpdatePushSettingRequest {

    @Schema(description = "타임레터 푸시 알림 수신 여부", example = "false")
    private Boolean timeLetter;

    @Schema(description = "마음의 기록 푸시 알림 수신 여부", example = "true")
    private Boolean mindRecord;

    @Schema(description = "애프터노트 푸시 알림 수신 여부", example = "false")
    private Boolean afterNote;
}