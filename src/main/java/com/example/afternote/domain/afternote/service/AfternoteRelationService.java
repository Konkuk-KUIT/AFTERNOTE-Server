package com.example.afternote.domain.afternote.service;

import com.example.afternote.domain.afternote.dto.AfternoteCreateRequest;
import com.example.afternote.domain.afternote.model.*;
import com.example.afternote.domain.afternote.repository.AfternotePlaylistRepository;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.repository.ReceivedRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import com.example.afternote.global.util.ChaChaEncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Afternote 카테고리별 관계 데이터 저장을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class AfternoteRelationService {

    private final ReceivedRepository receiverRepository;
    private final ChaChaEncryptionUtil chaChaEncryptionUtil;
    private final AfternotePlaylistRepository playlistRepository;

    /**
     * 카테고리별 관계 데이터 저장
     */
    public void saveRelationsByCategory(Afternote afternote, AfternoteCreateRequest request) {
        // 모든 카테고리에서 receiver 저장 (옵션)
        if (request.getReceivers() != null && !request.getReceivers().isEmpty()) {
            saveReceivers(afternote, request);
        }
        
        switch (request.getCategory()) {
            case SOCIAL:
                saveSocialCredentials(afternote, request);
                break;
            case GALLERY:
                // receivers는 위에서 이미 처리됨
                break;
            case PLAYLIST:
                savePlaylist(afternote, request);
                break;
        }
    }

    /**
     * SOCIAL 카테고리: credentials 저장 (암호화)
     */
    private void saveSocialCredentials(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getCredentials() == null) return;

        if (request.getCredentials().getId() != null) {
            String encryptedId = chaChaEncryptionUtil.encrypt(request.getCredentials().getId());
            afternote.getSecureContents().add(
                    createSecureContent(afternote, "account_id", encryptedId));
        }
        if (request.getCredentials().getPassword() != null) {
            String encryptedPassword = chaChaEncryptionUtil.encrypt(request.getCredentials().getPassword());
            afternote.getSecureContents().add(
                    createSecureContent(afternote, "account_password", encryptedPassword));
        }
    }
    
    /**
     * SOCIAL 카테고리: PATCH 업데이트 (제공된 필드만 업데이트, 암호화)
     */
    public void updateSocialCredentials(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getCredentials() == null) return;
        
        // ID 업데이트
        if (request.getCredentials().getId() != null) {
            String encryptedId = chaChaEncryptionUtil.encrypt(request.getCredentials().getId());
            // 기존 ID 삭제 후 새로 추가
            afternote.getSecureContents().removeIf(sc -> "account_id".equals(sc.getKeyName()));
            afternote.getSecureContents().add(
                    createSecureContent(afternote, "account_id", encryptedId));
        }
        
        // Password 업데이트
        if (request.getCredentials().getPassword() != null) {
            String encryptedPassword = chaChaEncryptionUtil.encrypt(request.getCredentials().getPassword());
            // 기존 Password 삭제 후 새로 추가
            afternote.getSecureContents().removeIf(sc -> "account_password".equals(sc.getKeyName()));
            afternote.getSecureContents().add(
                    createSecureContent(afternote, "account_password", encryptedPassword));
        }
    }

    /**
     * Receivers 저장 (모든 카테고리에서 사용 가능)
     */
    private void saveReceivers(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getReceivers() == null) return;

        for (AfternoteCreateRequest.ReceiverRequest receiverReq : request.getReceivers()) {
            Receiver receiver = receiverRepository.findById(receiverReq.getReceiverId())
                    .orElseThrow(() -> new CustomException(ErrorCode.RECEIVER_NOT_FOUND));

            afternote.getReceivers().add(createAfternoteReceiver(afternote, receiver));
        }
    }
    
    /**
     * GALLERY 카테고리: receivers 저장 (deprecated - saveReceivers 사용)
     */
    @Deprecated
    private void saveGalleryReceivers(Afternote afternote, AfternoteCreateRequest request) {
        saveReceivers(afternote, request);
    }
    
    /**
     * Receivers 업데이트 (모든 카테고리에서 사용 가능, 제공된 receivers가 있으면 전체 교체)
     */
    public void updateReceivers(Afternote afternote, AfternoteCreateRequest request) {
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
     * GALLERY 카테고리: PATCH 업데이트 (deprecated - updateReceivers 사용)
     */
    @Deprecated
    public void updateGalleryReceivers(Afternote afternote, AfternoteCreateRequest request) {
        updateReceivers(afternote, request);
    }

    /**
     * PLAYLIST 카테고리: playlist와 songs 저장
     */
    private void savePlaylist(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getPlaylist() == null) return;

        AfternotePlaylist.MemorialVideo memorialVideo = createMemorialVideo(request.getPlaylist().getMemorialVideo());
        AfternotePlaylist playlist = createPlaylist(afternote, request.getPlaylist().getAtmosphere(), memorialVideo);
        
        // playlist를 먼저 저장 (ID 생성)
        playlist = playlistRepository.save(playlist);

        // 플레이리스트 곡 추가
        if (request.getPlaylist().getSongs() != null) {
            int sortOrder = 1;
            for (AfternoteCreateRequest.SongRequest songReq : request.getPlaylist().getSongs()) {
                playlist.getItems().add(createPlaylistItem(playlist, songReq, sortOrder++));
            }
            // items 추가 후 다시 저장
            playlistRepository.save(playlist);
        }
    }
    
    /**
     * PLAYLIST 카테고리: PATCH 업데이트 (제공된 필드만 업데이트)
     */
    public void updatePlaylist(Afternote afternote, AfternoteCreateRequest request) {
        if (request.getPlaylist() == null) return;
        
        AfternotePlaylist playlist = afternote.getPlaylist();
        
        if (playlist == null) {
            // playlist가 없으면 새로 생성
            AfternotePlaylist newPlaylist = createPlaylist(
                    afternote,
                    request.getPlaylist().getAtmosphere(),
                    request.getPlaylist().getMemorialVideo() != null
                            ? createMemorialVideo(request.getPlaylist().getMemorialVideo())
                            : null
            );
            
            // 먼저 저장하여 ID 생성
            newPlaylist = playlistRepository.save(newPlaylist);
            
            if (request.getPlaylist().getSongs() != null) {
                int sortOrder = 1;
                for (AfternoteCreateRequest.SongRequest songReq : request.getPlaylist().getSongs()) {
                    newPlaylist.getItems().add(createPlaylistItem(newPlaylist, songReq, sortOrder++));
                }
                // items 추가 후 다시 저장
                playlistRepository.save(newPlaylist);
            }
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
        
        // 명시적 저장
        playlistRepository.save(playlist);
    }

    // ========== Builder Helper Methods ==========

    private AfternoteSecureContent createSecureContent(Afternote afternote, String keyName, String value) {
        return AfternoteSecureContent.builder()
                .afternote(afternote)
                .keyName(keyName)
                .encryptedValue(value)
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
