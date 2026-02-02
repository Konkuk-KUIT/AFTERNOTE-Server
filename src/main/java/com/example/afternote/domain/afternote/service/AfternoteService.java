package com.example.afternote.domain.afternote.service;

import com.example.afternote.domain.afternote.dto.*;
import com.example.afternote.domain.afternote.model.*;
import com.example.afternote.domain.afternote.repository.AfternoteRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import com.example.afternote.global.util.ChaChaEncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AfternoteService {

    private final AfternoteRepository afternoteRepository;
    private final AfternoteRelationService relationService;
    private final AfternoteValidator validator;
    private final ChaChaEncryptionUtil chaChaEncryptionUtil;

    public AfternotePageResponse getAfternotes(Long userId, AfternoteCategoryType category, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Afternote> afternotePage;
        
        if (category != null) {
            afternotePage = afternoteRepository.findByUserIdAndCategoryTypeOrderByCreatedAtDesc(userId, category, pageable);
        } else {
            afternotePage = afternoteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        
        List<AfternoteResponse> content = afternotePage.getContent().stream()
                .map(afternote -> AfternoteResponse.builder()
                        .afternoteId(afternote.getId())
                        .title(afternote.getTitle())
                        .category(afternote.getCategoryType())
                        .createdAt(afternote.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return AfternotePageResponse.builder()
                .content(content)
                .page(page)
                .size(size)
                .hasNext(afternotePage.hasNext())
                .build();
    }

    public AfternotedetailResponse getDetailAfternote(Long userId, Long afternoteId) {
        Afternote afternote = afternoteRepository.findById(afternoteId)
                .orElseThrow(() -> new CustomException(ErrorCode.AFTERNOTE_NOT_FOUND));
        if(!afternote.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.AFTERNOTE_NOT_FOUND);
        }
        
        AfternotedetailResponse response;
        
        // 카테고리별 데이터 조회 및 응답 생성
        switch (afternote.getCategoryType()) {
            case SOCIAL:
                // secureContents에서 credentials 가져오고 복호화
                AfternoteCreateRequest.CredentialsRequest credentials = null;
                
                String accountId = afternote.getSecureContents().stream()
                        .filter(sc -> "account_id".equals(sc.getKeyName()))
                        .findFirst()
                        .map(sc -> chaChaEncryptionUtil.decrypt(sc.getEncryptedValue()))
                        .orElse(null);
                
                String accountPassword = afternote.getSecureContents().stream()
                        .filter(sc -> "account_password".equals(sc.getKeyName()))
                        .findFirst()
                        .map(sc -> chaChaEncryptionUtil.decrypt(sc.getEncryptedValue()))
                        .orElse(null);
                
                if (accountId != null || accountPassword != null) {
                    credentials = new AfternoteCreateRequest.CredentialsRequest(accountId, accountPassword);
                }
                
                response = new AfternotedetailResponse(
                        afternote.getId(),
                        afternote.getCategoryType(),
                        afternote.getTitle(),
                        afternote.getProcessMethod(),
                        afternote.getActions(),
                        afternote.getLeaveMessage(),
                        credentials,
                        null,
                        null
                );
                break;
                
            case GALLERY:
                // receivers 매핑
                List<AfternoteCreateRequest.ReceiverRequest> receivers = afternote.getReceivers().stream()
                        .map(ar -> new AfternoteCreateRequest.ReceiverRequest(ar.getReceiver().getId()))
                        .collect(Collectors.toList());
                
                response = new AfternotedetailResponse(
                        afternote.getId(),
                        afternote.getCategoryType(),
                        afternote.getTitle(),
                        afternote.getProcessMethod(),
                        afternote.getActions(),
                        afternote.getLeaveMessage(),
                        null,
                        receivers,
                        null
                );
                break;
                
            case PLAYLIST:
                // playlist 매핑
                AfternoteCreateRequest.PlaylistRequest playlistRequest = null;
                
                if (!afternote.getPlaylists().isEmpty()) {
                    AfternotePlaylist playlist = afternote.getPlaylists().get(0);
                    
                    // songs 매핑
                    List<AfternoteCreateRequest.SongRequest> songs = playlist.getItems().stream()
                            .map(item -> new AfternoteCreateRequest.SongRequest(
                                    item.getSongTitle(),
                                    item.getArtist(),
                                    item.getCoverUrl()
                            ))
                            .collect(Collectors.toList());
                    
                    // memorialVideo 매핑
                    AfternoteCreateRequest.MemorialVideoRequest memorialVideo = null;
                    if (playlist.getMemorialVideo() != null) {
                        memorialVideo = new AfternoteCreateRequest.MemorialVideoRequest(
                                playlist.getMemorialVideo().getVideoUrl(),
                                playlist.getMemorialVideo().getThumbnailUrl()
                        );
                    }
                    
                    playlistRequest = new AfternoteCreateRequest.PlaylistRequest(
                            playlist.getAtmosphere(),
                            songs,
                            memorialVideo
                    );
                }
                
                response = new AfternotedetailResponse(
                        afternote.getId(),
                        afternote.getCategoryType(),
                        afternote.getTitle(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        playlistRequest
                );
                break;
                
            default:
                response = new AfternotedetailResponse(
                        afternote.getId(),
                        afternote.getCategoryType(),
                        afternote.getTitle(),
                        afternote.getProcessMethod(),
                        afternote.getActions(),
                        afternote.getLeaveMessage(),
                        null,
                        null,
                        null
                );
        }
        
        return response;
    }

    @Transactional
    public AfternoteCreateResponse createAfternote(Long userId, AfternoteCreateRequest request) {
        // 요청 데이터 검증
        validator.validateCreateRequest(request);
        
        // sortOrder 자동 계산 (해당 사용자의 최대값 + 1)
        Integer nextSortOrder = afternoteRepository.findMaxSortOrderByUserId(userId)
                .map(max -> max + 1)
                .orElse(1);
        
        // 공통 필드로 Afternote 생성
        Afternote.AfternoteBuilder builder = Afternote.builder()
                .userId(userId)
                .categoryType(request.getCategory())
                .title(request.getTitle())
                .sortOrder(nextSortOrder);
        
        // SOCIAL/GALLERY 전용 필드
        if (request.getCategory() == AfternoteCategoryType.SOCIAL || 
            request.getCategory() == AfternoteCategoryType.GALLERY) {
            builder.processMethod(request.getProcessMethod())
                   .actions(request.getActions() != null ? new ArrayList<>(request.getActions()) : new ArrayList<>())
                   .leaveMessage(request.getLeaveMessage());
        }
        
        Afternote afternote = builder.build();

        // 카테고리별 관계 데이터 저장
        relationService.saveRelationsByCategory(afternote, request);

        Afternote saved = afternoteRepository.save(afternote);
        
        return AfternoteCreateResponse.builder()
                .afternoteId(saved.getId())
                .build();
    }

    @Transactional
    public AfternoteCreateResponse updateAfternote(Long userId, Long afternoteId, AfternoteCreateRequest request) {
        Afternote afternote = afternoteRepository.findById(afternoteId)
                .orElseThrow(() -> new CustomException(ErrorCode.AFTERNOTE_NOT_FOUND));
        if(!afternote.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.AFTERNOTE_ACCESS_DENIED);
        }
        // PATCH용 검증 (카테고리 변경 불가 체크)
        validator.validateUpdateRequest(request, afternote.getCategoryType());

        // 기본 필드 업데이트 (null이 아닌 경우만)
        String title = request.getTitle() != null ? request.getTitle() : afternote.getTitle();
        String leaveMessage = null;
        ProcessMethod processMethod = null;
        List<String> actions = null;
        
        // SOCIAL/GALLERY 전용 필드 업데이트
        if (afternote.getCategoryType() == AfternoteCategoryType.SOCIAL || 
            afternote.getCategoryType() == AfternoteCategoryType.GALLERY) {
            leaveMessage = request.getLeaveMessage() != null ? request.getLeaveMessage() : afternote.getLeaveMessage();
            processMethod = request.getProcessMethod() != null ? request.getProcessMethod() : afternote.getProcessMethod();
            actions = request.getActions() != null ? request.getActions() : afternote.getActions();
        }
        
        afternote.update(title, afternote.getSortOrder(), leaveMessage, processMethod, actions);
        
        // 관계 데이터 업데이트 (제공된 경우만 PATCH 방식으로 업데이트)
        if (afternote.getCategoryType() == AfternoteCategoryType.SOCIAL && request.getCredentials() != null) {
            relationService.updateSocialCredentials(afternote, request);
        } else if (afternote.getCategoryType() == AfternoteCategoryType.GALLERY && request.getReceivers() != null) {
            relationService.updateGalleryReceivers(afternote, request);
        } else if (afternote.getCategoryType() == AfternoteCategoryType.PLAYLIST && request.getPlaylist() != null) {
            // PATCH 방식: 제공된 필드만 업데이트
            relationService.updatePlaylist(afternote, request);
        }
        
        return AfternoteCreateResponse.builder()
                .afternoteId(afternote.getId())
                .build();
    }

    @Transactional
    public void deleteAfternote(Long userId, Long afternoteId) {
        Afternote afternote = afternoteRepository.findById(afternoteId)
                .orElseThrow(() -> new CustomException(ErrorCode.AFTERNOTE_NOT_FOUND));
        if(!afternote.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.AFTERNOTE_ACCESS_DENIED);
        }
        afternoteRepository.delete(afternote);
    }
}
