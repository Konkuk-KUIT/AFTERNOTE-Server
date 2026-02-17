package com.example.afternote.domain.music.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetMusicSearchResponse {

    @Schema(description = "검색된 노래 목록")
    private List<MusicSearchItemDto> tracks;
}
