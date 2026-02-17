package com.example.afternote.domain.mindrecord.service;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.model.MindRecordReceiver;
import com.example.afternote.domain.receiver.repository.MindRecordReceiverRepository;
import com.example.afternote.domain.mindrecord.repository.MindRecordRepository;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.repository.ReceiverRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MindRecordReceiverService {

    private final MindRecordRepository mindRecordRepository;
    private final ReceiverRepository receiverRepository;
    private final MindRecordReceiverRepository mindRecordReceiverRepository;

    /**
     * 마음의 기록 전달 설정 토글 (ON / OFF)
     */
    public void toggleReceiver(
            Long userId, Long recordId, Long receiverId, boolean enabled
    ) {
        MindRecord record = mindRecordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.MIND_RECORD_FORBIDDEN);
        }

        Receiver receiver = receiverRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECEIVER_NOT_FOUND));

        if (!receiver.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.RECEIVER_FORBIDDEN);
        }

        mindRecordReceiverRepository
                .findByMindRecordAndReceiver(record, receiver)
                .ifPresentOrElse(
                        link -> {
                            // 이미 있으면 ON / OFF만 변경
                            if (enabled) {link.enable();}
                            else {link.disable();}
                        },
                        () -> {
                            // 없을 때는 ON 요청일 경우에만 생성
                            if (enabled) {
                                MindRecordReceiver link =
                                        MindRecordReceiver.create(record, receiver);
                                mindRecordReceiverRepository.save(link);
                            }
                        }
                );
    }
}