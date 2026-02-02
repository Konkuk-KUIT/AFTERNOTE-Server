package com.example.afternote.domain.receiver.service;

import com.example.afternote.domain.afternote.model.AfternoteReceiver;
import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.repository.MindRecordRepository;
import com.example.afternote.domain.receiver.dto.*;
import com.example.afternote.domain.receiver.model.MindRecordReceiver;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.model.TimeLetterReceiver;
import com.example.afternote.domain.receiver.repository.AfternoteReceiverRepository;
import com.example.afternote.domain.receiver.repository.MindRecordReceiverRepository;
import com.example.afternote.domain.receiver.repository.ReceiverRepository;
import com.example.afternote.domain.receiver.repository.TimeLetterReceiverRepository;
import com.example.afternote.domain.timeletter.model.TimeLetter;
import com.example.afternote.domain.timeletter.repository.TimeLetterRepository;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final MindRecordRepository mindRecordRepository;
    private final UserRepository userRepository;

    /**
     * 수신자가 받은 타임레터 목록 조회
     */
    public ReceivedTimeLetterListResponse getTimeLetters(Long receiverId) {
        validateReceiver(receiverId);

        List<TimeLetterReceiver> timeLetterReceivers =
                timeLetterReceiverRepository.findByReceiverIdWithTimeLetter(receiverId);

        List<ReceivedTimeLetterResponse> responses = timeLetterReceivers.stream()
                .map(ReceivedTimeLetterResponse::from)
                .toList();

        return ReceivedTimeLetterListResponse.from(responses);
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
     * 마인드레코드에 수신자 등록
     */
    @Transactional
    public List<Long> createMindRecordReceivers(Long userId, CreateMindRecordReceiverRequest request) {
        MindRecord mindRecord = mindRecordRepository.findById(request.getMindRecordId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        // 본인의 마인드레코드인지 확인
        if (!mindRecord.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_PERMISSION);
        }

        List<Receiver> receivers = receiverRepository.findAllById(request.getReceiverIds());
        if (receivers.size() != request.getReceiverIds().size()) {
            throw new CustomException(ErrorCode.RECEIVER_NOT_FOUND);
        }

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
     * 수신자 존재 여부 검증
     */
    private void validateReceiver(Long receiverId) {
        if (!receiverRepository.existsById(receiverId)) {
            throw new CustomException(ErrorCode.RECEIVER_NOT_FOUND);
        }
    }
}
