package com.example.afternote.domain.mindrecord.repository;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.model.MindRecordReceiver;
import com.example.afternote.domain.receiver.model.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MindRecordReceiverRepository
        extends JpaRepository<MindRecordReceiver, Long> {

    // 특정 마음의 기록과 수신인 사이의 연결 정보를 조회
    Optional<MindRecordReceiver> findByMindRecordAndReceiver(
            MindRecord mindRecord,
            Receiver receiver
    );

    // 특정 마음의 기록이 특정 수신인과 연결되어 있는지 여부를 확인
    boolean existsByMindRecordAndReceiver(
            MindRecord mindRecord,
            Receiver receiver
    );
}