package com.example.afternote.domain.afternote.service;

import com.example.afternote.domain.afternote.dto.AfternoteCreateRequest;
import com.example.afternote.domain.afternote.dto.AfternoteCreateResponse;
import com.example.afternote.domain.afternote.dto.AfternotePageResponse;
import com.example.afternote.domain.afternote.dto.AfternoteResponse;
import com.example.afternote.domain.afternote.model.*;
import com.example.afternote.domain.afternote.repository.AfternoteRepository;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.repository.ReceivedRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
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
                        .noteId(afternote.getId())
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
        
        afternoteRepository.delete(afternote);
    }
}
