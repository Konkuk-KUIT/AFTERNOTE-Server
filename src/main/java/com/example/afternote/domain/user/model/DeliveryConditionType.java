package com.example.afternote.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "전달 조건 타입 - 콘텐츠가 수신자에게 전달되는 조건을 지정합니다.")
public enum DeliveryConditionType {
    @Schema(description = "조건 없음 - 즉시 열람 가능")
    NONE,
    @Schema(description = "사망 확인 - 사망진단서 및 가족관계증명서 제출 후 관리자 승인 필요")
    DEATH_CERTIFICATE,
    @Schema(description = "비활동 감지 - 지정한 기간 동안 로그인하지 않으면 전달")
    INACTIVITY,
    @Schema(description = "특정 날짜 - 지정한 날짜가 지나면 전달")
    SPECIFIC_DATE
}
