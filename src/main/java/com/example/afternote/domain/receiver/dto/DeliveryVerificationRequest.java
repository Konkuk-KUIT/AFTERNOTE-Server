package com.example.afternote.domain.receiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사망확인 서류 제출 요청")
public class DeliveryVerificationRequest {

    @NotBlank(message = "사망진단서 URL은 필수입니다.")
    @Schema(description = "사망진단서 URL", example = "https://bucket.s3.region.amazonaws.com/documents/xxx.pdf")
    private String deathCertificateUrl;

    @NotBlank(message = "가족관계증명서 URL은 필수입니다.")
    @Schema(description = "가족관계증명서 URL", example = "https://bucket.s3.region.amazonaws.com/documents/yyy.pdf")
    private String familyRelationCertificateUrl;
}
