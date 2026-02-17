package com.example.afternote.domain.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MusicSearchItemDto {

    @Schema(description = "가수 이름", example = "IU")
    private String artist;

    @Schema(description = "노래 제목", example = "좋은날")
    private String title;

    @Schema(description = "앨범 이미지 URL",
            example = "https://is1-ssl.mzstatic.com/image/thumb/Music126/v4/.../100x100bb.jpg",
            nullable = true)
    private String albumImageUrl;
}
