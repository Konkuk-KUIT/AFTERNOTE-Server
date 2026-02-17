package com.example.afternote.domain.music.controller;

import com.example.afternote.domain.music.dto.GetMusicSearchResponse;
import com.example.afternote.domain.music.service.ItunesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/music")
public class MusicController {

    private final ItunesService itunesService;

    @Operation(summary = "노래 검색 API", description = "가수명 또는 노래 제목으로 노래를 검색")
    @GetMapping("/search")
    public GetMusicSearchResponse searchMusic(
            @Parameter(description = "검색어", example = "아이유", required = true)
            @RequestParam String keyword
    ) {
        return itunesService.searchMusic(keyword);
    }
}
