package com.example.afternote.domain.user.dto;

import com.example.afternote.domain.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserPushSettingResponse {

    @Schema(description = "타임레터 푸시 알림 수신 여부", example = "true")
    private boolean timeLetter;

    @Schema(description = "마음의 기록 푸시 알림 수신 여부", example = "false")
    private boolean mindRecord;

    @Schema(description = "애프터노트 푸시 알림 수신 여부", example = "true")
    private boolean afterNote;

    public static UserPushSettingResponse from(User user) {
        return UserPushSettingResponse.builder()
                .timeLetter(user.isTimeLetterPushEnabled())
                .mindRecord(user.isMindRecordPushEnabled())
                .afterNote(user.isAfterNotePushEnabled())
                .build();
    }
}