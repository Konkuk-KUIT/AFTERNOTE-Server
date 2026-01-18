package com.example.afternote.domain.timeletter.service;

import com.example.afternote.domain.timeletter.dto.*;
import com.example.afternote.domain.timeletter.model.TimeLetter;
import com.example.afternote.domain.timeletter.model.TimeLetterMedia;
import com.example.afternote.domain.timeletter.model.TimeLetterStatus;
import com.example.afternote.domain.timeletter.repository.TimeLetterMediaRepository;
import com.example.afternote.domain.timeletter.repository.TimeLetterRepository;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeLetterService {

    private final TimeLetterRepository timeLetterRepository;
    private final TimeLetterMediaRepository timeLetterMediaRepository;
    private final UserRepository userRepository;

    /**
     * 정식 등록된 타임레터 전체 조회 (SCHEDULED 상태만)
     */
    @Transactional(readOnly = true)
    public TimeLetterListResponse getTimeLetters(Long userId) {
        List<TimeLetter> timeLetters = timeLetterRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, TimeLetterStatus.SCHEDULED);
        System.out.println("timeLetters = " + timeLetters);
        List<TimeLetterResponse> responses = timeLetters.stream()
                .map(timeLetter -> {
                    List<TimeLetterMedia> mediaList = timeLetterMediaRepository.findByTimeLetterId(timeLetter.getId());
                    return TimeLetterResponse.from(timeLetter, mediaList);
                })
                .collect(Collectors.toList());

        return TimeLetterListResponse.from(responses);
    }

    /**
     * 타임레터 단일 조회
     */
    @Transactional(readOnly = true)
    public TimeLetterResponse getTimeLetter(Long userId, Long timeLetterId) {
        TimeLetter timeLetter = findTimeLetterWithOwnership(userId, timeLetterId);
        List<TimeLetterMedia> mediaList = timeLetterMediaRepository.findByTimeLetterId(timeLetterId);
        return TimeLetterResponse.from(timeLetter, mediaList);
    }

    /**
     * 타임레터 생성 (임시저장/정식등록)
     */
    @Transactional
    public TimeLetterResponse createTimeLetter(Long userId, TimeLetterCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // SCHEDULED 상태면 필수값 검증
        if (request.getStatus() == TimeLetterStatus.SCHEDULED) {
            validateForScheduled(request.getTitle(), request.getContent(), request.getSendAt());
        }

        TimeLetter timeLetter = TimeLetter.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .sendAt(request.getSendAt())
                .status(request.getStatus())
                .build();

        TimeLetter savedTimeLetter = timeLetterRepository.save(timeLetter);

        // 미디어 저장
        List<TimeLetterMedia> savedMediaList = saveMediaList(savedTimeLetter, request.getMediaList());

        return TimeLetterResponse.from(savedTimeLetter, savedMediaList);
    }

    /**
     * 임시저장된 타임레터 전체 조회 (DRAFT 상태만)
     */
    @Transactional(readOnly = true)
    public TimeLetterListResponse getTemporaryTimeLetters(Long userId) {
        List<TimeLetter> timeLetters = timeLetterRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, TimeLetterStatus.DRAFT);

        List<TimeLetterResponse> responses = timeLetters.stream()
                .map(timeLetter -> {
                    List<TimeLetterMedia> mediaList = timeLetterMediaRepository.findByTimeLetterId(timeLetter.getId());
                    return TimeLetterResponse.from(timeLetter, mediaList);
                })
                .collect(Collectors.toList());

        return TimeLetterListResponse.from(responses);
    }

    /**
     * 타임레터 삭제 (단일/다건)
     */
    @Transactional
    public void deleteTimeLetters(Long userId, TimeLetterDeleteRequest request) {
        List<TimeLetter> timeLetters = timeLetterRepository
                .findByIdInAndUserId(request.getTimeLetterIds(), userId);

        // 요청한 ID와 실제 조회된 수가 다르면 권한 없는 항목이 포함됨
        if (timeLetters.size() != request.getTimeLetterIds().size()) {
            throw new CustomException(ErrorCode.TIME_LETTER_NOT_FOUND);
        }

        // SENT 상태 검증
        for (TimeLetter timeLetter : timeLetters) {
            if (!timeLetter.isModifiable()) {
                throw new CustomException(ErrorCode.TIME_LETTER_ALREADY_SENT);
            }
        }

        // 미디어 삭제 후 타임레터 삭제
        timeLetterMediaRepository.deleteByTimeLetterIdIn(request.getTimeLetterIds());
        timeLetterRepository.deleteAll(timeLetters);
    }

    /**
     * 임시저장된 타임레터 전체 삭제
     */
    @Transactional
    public void deleteAllTemporary(Long userId) {
        List<TimeLetter> draftTimeLetters = timeLetterRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, TimeLetterStatus.DRAFT);

        if (!draftTimeLetters.isEmpty()) {
            List<Long> draftIds = draftTimeLetters.stream()
                    .map(TimeLetter::getId)
                    .collect(Collectors.toList());

            timeLetterMediaRepository.deleteByTimeLetterIdIn(draftIds);
            timeLetterRepository.deleteByUserIdAndStatus(userId, TimeLetterStatus.DRAFT);
        }
    }

    /**
     * 타임레터 수정
     */
    @Transactional
    public TimeLetterResponse updateTimeLetter(Long userId, Long timeLetterId, TimeLetterUpdateRequest request) {
        TimeLetter timeLetter = findTimeLetterWithOwnership(userId, timeLetterId);

        // SENT 상태면 수정 불가
        if (!timeLetter.isModifiable()) {
            throw new CustomException(ErrorCode.TIME_LETTER_ALREADY_SENT);
        }

        TimeLetterStatus newStatus = request.getStatus() != null ? request.getStatus() : timeLetter.getStatus();
        String newTitle = request.getTitle() != null ? request.getTitle() : timeLetter.getTitle();
        String newContent = request.getContent() != null ? request.getContent() : timeLetter.getContent();
        LocalDateTime newSendAt = request.getSendAt() != null ? request.getSendAt() : timeLetter.getSendAt();

        // SCHEDULED 상태로 변경하거나 이미 SCHEDULED인 경우 필수값 검증
        if (newStatus == TimeLetterStatus.SCHEDULED) {
            validateForScheduled(newTitle, newContent, newSendAt);
        }

        // 엔티티 업데이트
        timeLetter.update(request.getTitle(), request.getContent(), request.getSendAt(), request.getStatus());

        // 미디어 갱신 (기존 삭제 후 새로 저장)
        List<TimeLetterMedia> updatedMediaList;
        if (request.getMediaList() != null) {
            timeLetterMediaRepository.deleteByTimeLetterId(timeLetterId);
            updatedMediaList = saveMediaList(timeLetter, request.getMediaList());
        } else {
            updatedMediaList = timeLetterMediaRepository.findByTimeLetterId(timeLetterId);
        }

        return TimeLetterResponse.from(timeLetter, updatedMediaList);
    }

    /**
     * 소유권 검증 후 타임레터 조회
     */
    private TimeLetter findTimeLetterWithOwnership(Long userId, Long timeLetterId) {
        return timeLetterRepository.findByIdAndUserId(timeLetterId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TIME_LETTER_NOT_FOUND));
    }

    /**
     * SCHEDULED 상태 필수값 검증
     */
    private void validateForScheduled(String title, String content, LocalDateTime sendAt) {
        if (title == null || title.isBlank() || content == null || content.isBlank() || sendAt == null) {
            throw new CustomException(ErrorCode.TIME_LETTER_REQUIRED_FIELDS);
        }

        if (sendAt.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.TIME_LETTER_INVALID_SEND_DATE);
        }
    }

    /**
     * 미디어 리스트 저장
     */
    private List<TimeLetterMedia> saveMediaList(TimeLetter timeLetter, List<TimeLetterMediaRequest> mediaRequests) {
        if (mediaRequests == null || mediaRequests.isEmpty()) {
            return new ArrayList<>();
        }

        List<TimeLetterMedia> mediaList = mediaRequests.stream()
                .map(req -> TimeLetterMedia.builder()
                        .timeLetter(timeLetter)
                        .mediaType(req.getMediaType())
                        .mediaUrl(req.getMediaUrl())
                        .build())
                .collect(Collectors.toList());

        return timeLetterMediaRepository.saveAll(mediaList);
    }
}
