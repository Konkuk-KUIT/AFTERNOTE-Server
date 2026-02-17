package com.example.afternote.domain.music.service;

import com.example.afternote.domain.music.dto.GetMusicSearchResponse;
import com.example.afternote.domain.music.dto.MusicSearchItemDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItunesService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GetMusicSearchResponse searchMusic(String keyword) {

        String responseBody = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("itunes.apple.com")
                        .path("/search")
                        .queryParam("term", keyword)
                        .queryParam("media", "music")
                        .queryParam("entity", "song")
                        .queryParam("country", "KR")
                        .queryParam("lang", "ko_kr")
                        .queryParam("limit", 20)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            ItunesResponse response =
                    objectMapper.readValue(responseBody, ItunesResponse.class);

            if (response.results() == null) {
                return new GetMusicSearchResponse(List.of());
            }

            List<MusicSearchItemDto> tracks =
                    response.results().stream()
                            .filter(track ->
                                    track.artistName() != null &&
                                            track.trackName() != null
                            )
                            .map(track ->
                                    new MusicSearchItemDto(
                                            track.artistName(),
                                            track.trackName(),
                                            track.artworkUrl100()
                                    )
                            )
                            .toList();

            return new GetMusicSearchResponse(tracks);

        } catch (Exception e) {
            throw new RuntimeException("iTunes 응답 파싱 실패", e);
        }
    }


    private record ItunesResponse(
            List<ItunesTrack> results
    ) {}

    private record ItunesTrack(
            String artistName, String trackName, String artworkUrl100
    ) {}

}
