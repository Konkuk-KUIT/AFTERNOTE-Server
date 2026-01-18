package com.example.afternote.domain.timeletter.dto;

import com.example.afternote.domain.timeletter.model.MediaType;
import com.example.afternote.domain.timeletter.model.TimeLetterMedia;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "타임레터 미디어 응답")
@Getter
@Builder
public class TimeLetterMediaResponse {

    @Schema(description = "미디어 ID", example = "1")
    private Long id;

    @Schema(description = "미디어 타입", example = "IMAGE")
    private MediaType mediaType;

    @Schema(description = "미디어 URL", example = "https://example.com/image.jpg")
    private String mediaUrl;

    public static TimeLetterMediaResponse from(TimeLetterMedia media) {
        return TimeLetterMediaResponse.builder()
                .id(media.getId())
                .mediaType(media.getMediaType())
                .mediaUrl(media.getMediaUrl())
                .build();
    }
}
