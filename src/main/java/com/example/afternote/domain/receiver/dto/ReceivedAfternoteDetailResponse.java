package com.example.afternote.domain.receiver.dto;

import com.example.afternote.domain.afternote.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "수신한 애프터노트 상세 응답")
@Getter
@Builder
@AllArgsConstructor
public class ReceivedAfternoteDetailResponse {

    @Schema(description = "애프터노트 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리", example = "PLAYLIST")
    private AfternoteCategoryType category;

    @Schema(description = "제목", example = "내 아들에게")
    private String title;

    @Schema(description = "체크리스트 (SOCIAL/GALLERY 전용)")
    private List<String> actions;

    @Schema(description = "남기는 메시지")
    private String leaveMessage;

    @Schema(description = "발신자 이름", example = "김철수")
    private String senderName;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "플레이리스트 정보 (PLAYLIST 전용)")
    private PlaylistInfo playlist;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PlaylistInfo {
        @Schema(description = "분위기 설명")
        private String atmosphere;

        @Schema(description = "노래 목록")
        private List<SongInfo> songs;

        @Schema(description = "추모 영상")
        private MemorialVideoInfo memorialVideo;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SongInfo {
        @Schema(description = "곡 제목")
        private String title;

        @Schema(description = "아티스트")
        private String artist;

        @Schema(description = "커버 이미지 URL")
        private String coverUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemorialVideoInfo {
        @Schema(description = "영상 URL")
        private String videoUrl;

        @Schema(description = "썸네일 URL")
        private String thumbnailUrl;
    }

    public static ReceivedAfternoteDetailResponse fromSocial(Afternote afternote, String senderName) {
        return ReceivedAfternoteDetailResponse.builder()
                .id(afternote.getId())
                .category(afternote.getCategoryType())
                .title(afternote.getTitle())
                .actions(afternote.getActions())
                .leaveMessage(afternote.getLeaveMessage())
                .senderName(senderName)
                .createdAt(afternote.getCreatedAt())
                .build();
    }

    public static ReceivedAfternoteDetailResponse fromGallery(Afternote afternote, String senderName) {
        return ReceivedAfternoteDetailResponse.builder()
                .id(afternote.getId())
                .category(afternote.getCategoryType())
                .title(afternote.getTitle())
                .actions(afternote.getActions())
                .leaveMessage(afternote.getLeaveMessage())
                .senderName(senderName)
                .createdAt(afternote.getCreatedAt())
                .build();
    }

    public static ReceivedAfternoteDetailResponse fromPlaylist(Afternote afternote, String senderName) {
        PlaylistInfo playlistInfo = null;

        if (afternote.getPlaylist() != null) {
            AfternotePlaylist pl = afternote.getPlaylist();

            List<SongInfo> songs = pl.getItems().stream()
                    .map(item -> SongInfo.builder()
                            .title(item.getSongTitle())
                            .artist(item.getArtist())
                            .coverUrl(item.getCoverUrl())
                            .build())
                    .toList();

            MemorialVideoInfo video = null;
            if (pl.getMemorialVideo() != null) {
                video = MemorialVideoInfo.builder()
                        .videoUrl(pl.getMemorialVideo().getVideoUrl())
                        .thumbnailUrl(pl.getMemorialVideo().getThumbnailUrl())
                        .build();
            }

            playlistInfo = PlaylistInfo.builder()
                    .atmosphere(pl.getAtmosphere())
                    .songs(songs)
                    .memorialVideo(video)
                    .build();
        }

        return ReceivedAfternoteDetailResponse.builder()
                .id(afternote.getId())
                .category(afternote.getCategoryType())
                .title(afternote.getTitle())
                .senderName(senderName)
                .createdAt(afternote.getCreatedAt())
                .playlist(playlistInfo)
                .build();
    }
}
