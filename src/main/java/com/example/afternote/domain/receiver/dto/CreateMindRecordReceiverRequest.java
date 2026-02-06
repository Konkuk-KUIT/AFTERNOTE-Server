package com.example.afternote.domain.receiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "마인드레코드 수신자 등록 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMindRecordReceiverRequest {

    @NotNull(message = "마인드레코드 ID는 필수입니다.")
    @Schema(description = "마인드레코드 ID", example = "1")
    private Long mindRecordId;

    @NotEmpty(message = "수신자 ID 목록은 필수입니다.")
    @Schema(description = "수신자 ID 목록", example = "[1, 2, 3]")
    private List<Long> receiverIds;
}
