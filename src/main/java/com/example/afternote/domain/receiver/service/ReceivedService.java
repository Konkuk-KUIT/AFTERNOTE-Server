package com.example.afternote.domain.receiver.service;

import com.example.afternote.domain.afternote.model.AfternoteReceiver;
import com.example.afternote.domain.image.service.S3Service;
import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.repository.MindRecordRepository;
import com.example.afternote.domain.receiver.dto.*;
import com.example.afternote.domain.mindrecord.model.MindRecordReceiver;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.model.TimeLetterReceiver;
import com.example.afternote.domain.receiver.repository.AfternoteReceiverRepository;
import com.example.afternote.domain.receiver.repository.MindRecordReceiverRepository;
import com.example.afternote.domain.receiver.repository.ReceiverRepository;
import com.example.afternote.domain.receiver.repository.TimeLetterReceiverRepository;
import com.example.afternote.domain.timeletter.model.TimeLetter;
import com.example.afternote.domain.timeletter.model.TimeLetterMedia;
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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceivedService {

    private final ReceiverRepository receiverRepository;
    private final TimeLetterReceiverRepository timeLetterReceiverRepository;
    private final AfternoteReceiverRepository afternoteReceiverRepository;
    private final MindRecordReceiverRepository mindRecordReceiverRepository;
    private final TimeLetterRepository timeLetterRepository;
    private final TimeLetterMediaRepository timeLetterMediaRepository;
    private final MindRecordRepository mindRecordRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    /**
     * 수신자가 받은 타임레터 목록 조회 (미디어 포함)
     */
    public ReceivedTimeLetterListResponse getTimeLetters(Long receiverId) {
        validateReceiver(receiverId);

        List<TimeLetterReceiver> timeLetterReceivers =
                timeLetterReceiverRepository.findByReceiverIdWithTimeLetter(receiverId);

        // 타임레터 ID 수집 후 미디어 일괄 조회 (N+1 방지)
        List<Long> timeLetterIds = timeLetterReceivers.stream()
                .map(tlr -> tlr.getTimeLetter().getId())
                .toList();

        Map<Long, List<TimeLetterMedia>> mediaMap = timeLetterIds.isEmpty()
                ? Collections.emptyMap()
                : timeLetterMediaRepository.findByTimeLetterIdIn(timeLetterIds).stream()
                        .collect(Collectors.groupingBy(media -> media.getTimeLetter().getId()));

        List<ReceivedTimeLetterResponse> responses = timeLetterReceivers.stream()
                .map(tlr -> ReceivedTimeLetterResponse.from(
                        tlr,
                        mediaMap.getOrDefault(tlr.getTimeLetter().getId(), List.of()),
                        s3Service::generateGetPresignedUrl))
                .toList();

        return ReceivedTimeLetterListResponse.from(responses);
    }

    /**
     * 수신한 타임레터 상세 조회 (읽음 처리 포함)
     */
    @Transactional
    public ReceivedTimeLetterResponse getTimeLetter(Long receiverId, Long timeLetterReceiverId) {
        validateReceiver(receiverId);

        TimeLetterReceiver timeLetterReceiver = timeLetterReceiverRepository
                .findByIdAndReceiverIdWithTimeLetter(timeLetterReceiverId, receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.TIME_LETTER_NOT_FOUND));

        // 읽음 처리 (멱등성 보장 — dirty checking으로 자동 UPDATE)
        timeLetterReceiver.markAsRead();

        // 미디어 조회
        List<TimeLetterMedia> mediaList = timeLetterMediaRepository
                .findByTimeLetterId(timeLetterReceiver.getTimeLetter().getId());

        return ReceivedTimeLetterResponse.from(timeLetterReceiver, mediaList, s3Service::generateGetPresignedUrl);
    }

    /**
     * 수신자가 받은 애프터노트 목록 조회
     */
    public ReceivedAfternoteListResponse getAfternotes(Long receiverId) {
        validateReceiver(receiverId);

        List<AfternoteReceiver> afternoteReceivers =
                afternoteReceiverRepository.findByReceiverIdWithAfternote(receiverId);

        // Afternote는 userId만 가지고 있으므로 User 정보를 별도로 조회
        Set<Long> userIds = afternoteReceivers.stream()
                .map(ar -> ar.getAfternote().getUserId())
                .collect(Collectors.toSet());

        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<ReceivedAfternoteResponse> responses = afternoteReceivers.stream()
                .map(ar -> {
                    User sender = userMap.get(ar.getAfternote().getUserId());
                    String senderName = sender != null ? sender.getName() : "알 수 없음";
                    return ReceivedAfternoteResponse.from(ar, senderName);
                })
                .toList();

        return ReceivedAfternoteListResponse.from(responses);
    }

    /**
     * 수신자가 받은 마인드레코드 목록 조회
     */
    public ReceivedMindRecordListResponse getMindRecords(Long receiverId) {
        validateReceiver(receiverId);

        List<MindRecordReceiver> mindRecordReceivers =
                mindRecordReceiverRepository.findByReceiverIdWithMindRecord(receiverId);

        List<ReceivedMindRecordResponse> responses = mindRecordReceivers.stream()
                .map(ReceivedMindRecordResponse::from)
                .toList();

        return ReceivedMindRecordListResponse.from(responses);
    }

    /**
     * 타임레터에 수신자 등록
     */
    @Transactional
    public List<Long> createTimeLetterReceivers(Long userId, CreateTimeLetterReceiverRequest request) {
        TimeLetter timeLetter = timeLetterRepository.findByIdAndUserId(request.getTimeLetterID(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TIME_LETTER_NOT_FOUND));

        List<Receiver> receivers = receiverRepository.findAllById(request.getReceiverIds());
        if (receivers.size() != request.getReceiverIds().size()) {
            throw new CustomException(ErrorCode.RECEIVER_NOT_FOUND);
        }

        // 본인이 등록한 수신자인지 검증
        validateReceiversOwnership(userId, receivers);

        LocalDateTime deliveredAt = request.getDeliveredAt() != null
                ? request.getDeliveredAt()
                : timeLetter.getSendAt();

        List<TimeLetterReceiver> timeLetterReceivers = receivers.stream()
                .map(receiver -> TimeLetterReceiver.builder()
                        .timeLetter(timeLetter)
                        .receiver(receiver)
                        .deliveredAt(deliveredAt)
                        .build())
                .toList();

        return timeLetterReceiverRepository.saveAll(timeLetterReceivers).stream()
                .map(TimeLetterReceiver::getId)
                .toList();
    }

    /**
     * 타임레터 생성 시 수신자 등록 (오버로드 - TimeLetterService에서 호출)
     * 전제조건: 호출자가 TimeLetter의 소유권(userId 일치)을 이미 검증한 상태에서 호출해야 함.
     */
    @Transactional
    public List<Long> createTimeLetterReceivers(TimeLetter timeLetter, Long userId, List<Long> receiverIds, LocalDateTime deliveredAt) {
        // null 원소 필터링 + 중복 제거
        List<Long> uniqueIds = new ArrayList<>(new LinkedHashSet<>(
                receiverIds.stream().filter(Objects::nonNull).toList()));

        List<Receiver> receivers = receiverRepository.findAllById(uniqueIds);
        if (receivers.size() != uniqueIds.size()) {
            throw new CustomException(ErrorCode.RECEIVER_NOT_FOUND);
        }

        // 본인이 등록한 수신자인지 검증
        validateReceiversOwnership(userId, receivers);

        // deliveredAt이 null이면 timeLetter의 sendAt으로 폴백
        LocalDateTime effectiveDeliveredAt = deliveredAt != null ? deliveredAt : timeLetter.getSendAt();
        if (effectiveDeliveredAt == null) {
            throw new CustomException(ErrorCode.TIME_LETTER_REQUIRED_FIELDS);
        }

        List<TimeLetterReceiver> timeLetterReceivers = receivers.stream()
                .map(receiver -> TimeLetterReceiver.builder()
                        .timeLetter(timeLetter)
                        .receiver(receiver)
                        .deliveredAt(effectiveDeliveredAt)
                        .build())
                .toList();

        return timeLetterReceiverRepository.saveAll(timeLetterReceivers).stream()
                .map(TimeLetterReceiver::getId)
                .toList();
    }

    /**
     * 마인드레코드에 수신자 등록
     */
    @Transactional
    public List<Long> createMindRecordReceivers(Long userId, CreateMindRecordReceiverRequest request) {
        MindRecord mindRecord = mindRecordRepository.findById(request.getMindRecordId())
                .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));

        // 본인의 마인드레코드인지 확인
        if (!mindRecord.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_PERMISSION);
        }

        List<Receiver> receivers = receiverRepository.findAllById(request.getReceiverIds());
        if (receivers.size() != request.getReceiverIds().size()) {
            throw new CustomException(ErrorCode.RECEIVER_NOT_FOUND);
        }

        // 본인이 등록한 수신자인지 검증
        validateReceiversOwnership(userId, receivers);

        List<MindRecordReceiver> mindRecordReceivers = receivers.stream()
                .map(receiver -> MindRecordReceiver.builder()
                        .mindRecord(mindRecord)
                        .receiver(receiver)
                        .build())
                .toList();

        return mindRecordReceiverRepository.saveAll(mindRecordReceivers).stream()
                .map(MindRecordReceiver::getId)
                .toList();
    }

    /**
     * 수신자가 본인이 등록한 수신자인지 검증
     */
    private void validateReceiversOwnership(Long userId, List<Receiver> receivers) {
        boolean hasUnauthorizedReceiver = receivers.stream()
                .anyMatch(receiver -> !receiver.getUserId().equals(userId));

        if (hasUnauthorizedReceiver) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_PERMISSION);
        }
    }

    /**
     * 수신자 존재 여부 검증
     */
    private void validateReceiver(Long receiverId) {
        if (!receiverRepository.existsById(receiverId)) {
            throw new CustomException(ErrorCode.RECEIVER_NOT_FOUND);
        }
    }
}
