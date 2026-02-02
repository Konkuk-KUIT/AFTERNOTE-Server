package com.example.afternote.domain.afternote.dto;

import com.example.afternote.domain.afternote.model.AfternoteCategoryType;
import com.example.afternote.domain.afternote.model.ProcessMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AfternotedetailResponse {

    @Schema(description = "애프터노트 아이디", example = "1")
    private Long afternoteId;

    @Schema(description = "카테고리", example = "SOCIAL")
    private AfternoteCategoryType category;

    @Schema(description = "제목", example = "인스타그램")
    private String title;

    @Schema(description = "처리 방식 (SOCIAL/GALLERY 전용)", example = "MEMORIAL")
    private ProcessMethod processMethod;

    @Schema(description = "체크리스트 (SOCIAL/GALLERY 전용)")
    private List<String> actions;

    @Schema(description = "남기신 말씀 (SOCIAL/GALLERY 전용)")
    private String leaveMessage;

    @Schema(description = "계정 정보 (SOCIAL 전용)")
    private AfternoteCreateRequest.CredentialsRequest credentials;

    @Schema(description = "수신자 목록 (GALLERY 전용)")
    private List<AfternoteCreateRequest.ReceiverRequest> receivers;

    @Schema(description = "플레이리스트 정보 (Playlist 전용)")
    private AfternoteCreateRequest.PlaylistRequest playlist;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CredentialsRequest {
        @Schema(description = "아이디", example = "my_insta_id")
        private String id;

        @Schema(description = "비밀번호", example = "password123")
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiverRequest {
        @Schema(description = "수신자 ID", example = "1")
        private Long receiverId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaylistRequest {
        @Schema(description = "분위기 설명", example = "차분하고 조용하게 보내주세요.")
        private String atmosphere;

        @Schema(description = "노래 목록")
        private List<AfternoteCreateRequest.SongRequest> songs;

        @Schema(description = "추모 영상")
        private AfternoteCreateRequest.MemorialVideoRequest memorialVideo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SongRequest {
        @Schema(description = "곡 제목", example = "보고싶다")
        private String title;

        @Schema(description = "아티스트", example = "김범수")
        private String artist;

        @Schema(description = "커버 이미지 URL")
        private String coverUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemorialVideoRequest {
        @Schema(description = "영상 URL")
        private String videoUrl;

        @Schema(description = "썸네일 URL")
        private String thumbnailUrl;
    }
}
