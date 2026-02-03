package com.example.afternote.domain.afternote.service;

import com.example.afternote.domain.afternote.dto.AfternoteCreateRequest;
import com.example.afternote.domain.afternote.model.AfternoteCategoryType;
import com.example.afternote.domain.afternote.repository.AfternoteRepository;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Afternote 카테고리별 필드 검증
 */
@Component
@RequiredArgsConstructor
public class AfternoteValidator {

    private final AfternoteRepository afternoteRepository;
    private final UserRepository userRepository;

    /**
     * POST 요청 검증
     * - 있어야 하는 필드: 무조건 있어야 함
     * - 없어야 하는 필드: 무조건 null
     */
    public void validateCreateRequest(AfternoteCreateRequest request) {
        if (request.getCategory() == null) {
            throw new CustomException(ErrorCode.CATEGORY_REQUIRED);
        }

        switch (request.getCategory()) {
            case SOCIAL:
                validateSocialCreate(request);
                break;
            case GALLERY:
                validateGalleryCreate(request);
                break;
            case PLAYLIST:
                validatePlaylistCreate(request);
                break;
        }
    }

    /**
     * PATCH 요청 검증
     * - 없어야 하는 필드: 있으면 안됨
     * - 있어야 하는 필드: 있든 없든 상관없음
     */
    public void validateUpdateRequest(AfternoteCreateRequest request, AfternoteCategoryType category) {
        // 카테고리 변경 불가
        if (request.getCategory() != null && request.getCategory() != category) {
            throw new CustomException(ErrorCode.CATEGORY_CANNOT_BE_CHANGED);
        }

        switch (category) {
            case SOCIAL:
                validateSocialUpdate(request);
                break;
            case GALLERY:
                validateGalleryUpdate(request);
                break;
            case PLAYLIST:
                validatePlaylistUpdate(request);
                break;
        }
    }

    // ========== SOCIAL 검증 ==========

    private void validateSocialCreate(AfternoteCreateRequest request) {
        // 없어야 하는 필드
        if (request.getReceivers() != null && !request.getReceivers().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_SOCIAL);
        }
        if (request.getPlaylist() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_SOCIAL);
        }

        // 있어야 하는 필드
        if (request.getProcessMethod() == null) {
            throw new CustomException(ErrorCode.PROCESS_METHOD_REQUIRED);
        }
        if (request.getActions() == null || request.getActions().isEmpty()) {
            throw new CustomException(ErrorCode.ACTIONS_REQUIRED);
        }
        if (request.getCredentials() == null) {
            throw new CustomException(ErrorCode.SOCIAL_CREDENTIALS_REQUIRED);
        }
        if (request.getCredentials().getId() == null || request.getCredentials().getId().isBlank()) {
            throw new CustomException(ErrorCode.SOCIAL_ACCOUNT_ID_REQUIRED);
        }
        if (request.getCredentials().getPassword() == null || request.getCredentials().getPassword().isBlank()) {
            throw new CustomException(ErrorCode.SOCIAL_ACCOUNT_PASSWORD_REQUIRED);
        }
    }

    private void validateSocialUpdate(AfternoteCreateRequest request) {
        // 없어야 하는 필드
        if (request.getReceivers() != null && !request.getReceivers().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_SOCIAL);
        }
        if (request.getPlaylist() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_SOCIAL);
        }
    }

    // ========== GALLERY 검증 ==========

    private void validateGalleryCreate(AfternoteCreateRequest request) {
        // 없어야 하는 필드
        if (request.getCredentials() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_GALLERY);
        }
        if (request.getPlaylist() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_GALLERY);
        }

        // 있어야 하는 필드
        if (request.getProcessMethod() == null) {
            throw new CustomException(ErrorCode.PROCESS_METHOD_REQUIRED);
        }
        if (request.getActions() == null || request.getActions().isEmpty()) {
            throw new CustomException(ErrorCode.ACTIONS_REQUIRED);
        }
        if (request.getReceivers() == null || request.getReceivers().isEmpty()) {
            throw new CustomException(ErrorCode.GALLERY_RECEIVERS_REQUIRED);
        }
        for (AfternoteCreateRequest.ReceiverRequest receiver : request.getReceivers()) {
            if (receiver.getReceiverId() == null) {
                throw new CustomException(ErrorCode.GALLERY_RECEIVER_ID_REQUIRED);
            }
        }
    }

    private void validateGalleryUpdate(AfternoteCreateRequest request) {
        // 없어야 하는 필드
        if (request.getCredentials() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_GALLERY);
        }
        if (request.getPlaylist() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_GALLERY);
        }
    }

    // ========== PLAYLIST 검증 ==========

    private void validatePlaylistCreate(AfternoteCreateRequest request) {
        // 없어야 하는 필드
        if (request.getCredentials() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }
        if (request.getReceivers() != null && !request.getReceivers().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }
        if (request.getProcessMethod() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }
        if (request.getActions() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }
        if (request.getLeaveMessage() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }

        // 있어야 하는 필드
        if (request.getPlaylist() == null) {
            throw new CustomException(ErrorCode.PLAYLIST_REQUIRED);
        }
        if (request.getPlaylist().getSongs() == null || request.getPlaylist().getSongs().isEmpty()) {
            throw new CustomException(ErrorCode.PLAYLIST_SONGS_REQUIRED);
        }
        for (AfternoteCreateRequest.SongRequest song : request.getPlaylist().getSongs()) {
            if (song.getTitle() == null || song.getTitle().isBlank()) {
                throw new CustomException(ErrorCode.PLAYLIST_SONG_TITLE_REQUIRED);
            }
            if (song.getArtist() == null || song.getArtist().isBlank()) {
                throw new CustomException(ErrorCode.PLAYLIST_SONG_ARTIST_REQUIRED);
            }
        }
    }

    private void validatePlaylistUpdate(AfternoteCreateRequest request) {
        // 없어야 하는 필드
        if (request.getCredentials() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }
        if (request.getReceivers() != null && !request.getReceivers().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }
        if (request.getProcessMethod() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }
        if (request.getActions() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }
        if (request.getLeaveMessage() != null) {
            throw new CustomException(ErrorCode.INVALID_FIELD_FOR_PLAYLIST);
        }
    }
}
