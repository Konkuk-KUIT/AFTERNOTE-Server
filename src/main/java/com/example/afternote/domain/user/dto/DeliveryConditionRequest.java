package com.example.afternote.domain.user.dto;

import com.example.afternote.domain.user.model.DeliveryConditionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "전달 조건 설정 요청")
public class DeliveryConditionRequest {

    @NotNull(message = "전달 조건 타입은 필수입니다.")
    @Schema(description = "전달 조건 타입", example = "INACTIVITY")
    private DeliveryConditionType conditionType;

    @Schema(description = "비활동 기간 (일)", example = "365", nullable = true)
    private Integer inactivityPeriodDays;

    @Schema(description = "특정 날짜", example = "2027-01-01", nullable = true)
    private LocalDate specificDate;
}
