package com.example.afternote.domain.afternote.dto;

import com.example.afternote.domain.afternote.model.AfternoteCategoryType;
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




}
