package com.example.afternote.domain.afternote.service;

import com.example.afternote.domain.afternote.dto.AfternoteCreateRequest;
import com.example.afternote.domain.afternote.model.*;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.repository.ReceivedRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Afternote 카테고리별 관계 데이터 저장을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class AfternoteRelationService {

    private final ReceivedRepository receiverRepository;

    /**
     * 카테고리별 관계 데이터 저장
     */
    public void saveRelationsByCategory(Afternote afternote, AfternoteCreateRequest request) {
        switch (request.getCategory()) {
            case SOCIAL:
                saveSocialCredentials(afternote, request);
                break;
            case GALLERY:
                saveGalleryReceivers(afternote, request);
                break;
            case PLAYLIST:
                savePlaylist(afternote, request);
                break;
        }
    }

    /**
     * SOCIAL 카테고리: credentials 저장
     */
    private void saveSocialCredentials(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getCredentials() == null) return;

        if (request.getCredentials().getId() != null) {
            afternote.getSecureContents().add(
                    createSecureContent(afternote, "account_id", request.getCredentials().getId()));
        }
        if (request.getCredentials().getPassword() != null) {
            afternote.getSecureContents().add(
                    createSecureContent(afternote, "account_password", request.getCredentials().getPassword()));
        }
    }
    
    /**
     * SOCIAL 카테고리: PATCH 업데이트 (제공된 필드만 업데이트)
     */
    public void updateSocialCredentials(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getCredentials() == null) return;
        
        // ID 업데이트
        if (request.getCredentials().getId() != null) {
            // 기존 ID 삭제 후 새로 추가
            afternote.getSecureContents().removeIf(sc -> "account_id".equals(sc.getKeyName()));
            afternote.getSecureContents().add(
                    createSecureContent(afternote, "account_id", request.getCredentials().getId()));
        }
        
        // Password 업데이트
        if (request.getCredentials().getPassword() != null) {
            // 기존 Password 삭제 후 새로 추가
            afternote.getSecureContents().removeIf(sc -> "account_password".equals(sc.getKeyName()));
            afternote.getSecureContents().add(
                    createSecureContent(afternote, "account_password", request.getCredentials().getPassword()));
        }
    }

    /**
     * GALLERY 카테고리: receivers 저장
     */
    private void saveGalleryReceivers(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getReceivers() == null) return;

        for (AfternoteCreateRequest.ReceiverRequest receiverReq : request.getReceivers()) {
            Receiver receiver = receiverRepository.findById(receiverReq.getReceiverId())
                    .orElseThrow(() -> new CustomException(ErrorCode.RECEIVER_NOT_FOUND));

            afternote.getReceivers().add(createAfternoteReceiver(afternote, receiver));
        }
    }
    
    /**
     * GALLERY 카테고리: PATCH 업데이트 (제공된 필드만 업데이트)
     */
    public void updateGalleryReceivers(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getReceivers() == null) return;
        
        // receivers가 제공된 경우에만 전체 교체
        afternote.getReceivers().clear();
        for (AfternoteCreateRequest.ReceiverRequest receiverReq : request.getReceivers()) {
            Receiver receiver = receiverRepository.findById(receiverReq.getReceiverId())
                    .orElseThrow(() -> new CustomException(ErrorCode.RECEIVER_NOT_FOUND));

            afternote.getReceivers().add(createAfternoteReceiver(afternote, receiver));
        }
    }

    /**
     * PLAYLIST 카테고리: playlist와 songs 저장
     */
    private void savePlaylist(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getPlaylist() == null) return;

        AfternotePlaylist.MemorialVideo memorialVideo = createMemorialVideo(request.getPlaylist().getMemorialVideo());
        AfternotePlaylist playlist = createPlaylist(afternote, request.getPlaylist().getAtmosphere(), memorialVideo);

        // 플레이리스트 곡 추가
        if (request.getPlaylist().getSongs() != null) {
            int sortOrder = 1;
            for (AfternoteCreateRequest.SongRequest songReq : request.getPlaylist().getSongs()) {
                playlist.getItems().add(createPlaylistItem(playlist, songReq, sortOrder++));
            }
        }

        afternote.getPlaylists().add(playlist);
    }
    
    /**
     * PLAYLIST 카테고리: PATCH 업데이트 (제공된 필드만 업데이트)
     */
    public void updatePlaylist(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getPlaylist() == null) return;
        
        // 첫 번째 playlist 가져오기 (현재 구조상 하나만 존재)
        AfternotePlaylist playlist = afternote.getPlaylists().isEmpty() 
            ? null : afternote.getPlaylists().get(0);
        
        if (playlist == null) {
            // playlist가 없으면 새로 생성
            savePlaylist(afternote, request);
            return;
        }
        
        // atmosphere와 memorialVideo 업데이트
        AfternotePlaylist.MemorialVideo memorialVideo = request.getPlaylist().getMemorialVideo() != null
            ? createMemorialVideo(request.getPlaylist().getMemorialVideo())
            : null;
        playlist.update(request.getPlaylist().getAtmosphere(), memorialVideo);
        
        // songs가 제공된 경우에만 곡 목록 업데이트
        if (request.getPlaylist().getSongs() != null) {
            playlist.getItems().clear();
            int sortOrder = 1;
            for (AfternoteCreateRequest.SongRequest songReq : request.getPlaylist().getSongs()) {
                playlist.getItems().add(createPlaylistItem(playlist, songReq, sortOrder++));
            }
        }
    }

    // ========== Builder Helper Methods ==========

    private AfternoteSecureContent createSecureContent(Afternote afternote, String keyName, String value) {
        return AfternoteSecureContent.builder()
                .afternote(afternote)
                .keyName(keyName)
                .encryptedValue(value) // TODO: 암호화 필요
                .build();
    }

    private AfternoteReceiver createAfternoteReceiver(Afternote afternote, Receiver receiver) {
        return AfternoteReceiver.builder()
                .afternote(afternote)
                .receiver(receiver)
                .build();
    }

    private AfternotePlaylist.MemorialVideo createMemorialVideo(AfternoteCreateRequest.MemorialVideoRequest request) {
        if (request == null) return null;

        return AfternotePlaylist.MemorialVideo.builder()
                .videoUrl(request.getVideoUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .build();
    }

    private AfternotePlaylist createPlaylist(Afternote afternote, String atmosphere, AfternotePlaylist.MemorialVideo memorialVideo) {
        return AfternotePlaylist.builder()
                .afternote(afternote)
                .title(atmosphere != null ? atmosphere : "추모 플레이리스트")
                .atmosphere(atmosphere)
                .memorialVideo(memorialVideo)
                .build();
    }

    private AfternotePlaylistItem createPlaylistItem(AfternotePlaylist playlist, AfternoteCreateRequest.SongRequest song, int sortOrder) {
        return AfternotePlaylistItem.builder()
                .playlist(playlist)
                .songTitle(song.getTitle())
                .artist(song.getArtist())
                .coverUrl(song.getCoverUrl())
                .sortOrder(sortOrder)
                .build();
    }
}
