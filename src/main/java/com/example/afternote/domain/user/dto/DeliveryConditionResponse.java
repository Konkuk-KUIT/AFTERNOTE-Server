package com.example.afternote.domain.user.dto;

import com.example.afternote.domain.user.model.DeliveryConditionType;
import com.example.afternote.domain.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "전달 조건 설정 응답")
public record DeliveryConditionResponse(
        @Schema(description = "전달 조건 타입", example = "INACTIVITY")
        DeliveryConditionType conditionType,
        @Schema(description = "비활동 기간 (일)", example = "365", nullable = true)
        Integer inactivityPeriodDays,
        @Schema(description = "특정 날짜", example = "2027-01-01", nullable = true)
        LocalDate specificDate,
        @Schema(description = "조건 충족 여부", example = "false")
        boolean conditionFulfilled,
        @Schema(description = "현재 조건 충족 평가 결과", example = "false")
        boolean conditionMet
) {
    public static DeliveryConditionResponse from(User user) {
        return new DeliveryConditionResponse(
                user.getDeliveryConditionType(),
                user.getInactivityPeriodDays(),
                user.getSpecificDate(),
                user.isConditionFulfilled(),
                user.isDeliveryConditionMet()
        );
    }
}
