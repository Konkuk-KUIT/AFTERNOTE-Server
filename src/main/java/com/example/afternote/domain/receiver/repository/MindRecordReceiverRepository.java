package com.example.afternote.domain.receiver.repository;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.model.MindRecordReceiver;
import com.example.afternote.domain.receiver.model.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MindRecordReceiverRepository extends JpaRepository<MindRecordReceiver, Long> {

    @Query("""
            SELECT mrr FROM MindRecordReceiver mrr
            JOIN FETCH mrr.mindRecord mr
            JOIN FETCH mr.user
            WHERE mrr.receiver.id = :receiverId
            ORDER BY mr.createdAt DESC
            """)
    List<MindRecordReceiver> findByReceiverIdWithMindRecord(@Param("receiverId") Long receiverId);

    @Query("""
            SELECT mrr FROM MindRecordReceiver mrr
            JOIN FETCH mrr.mindRecord mr
            JOIN FETCH mr.user
            WHERE mr.id = :mindRecordId AND mrr.receiver.id = :receiverId
            AND mrr.enabled = true
            """)
    Optional<MindRecordReceiver> findByMindRecordIdAndReceiverIdWithMindRecord(
            @Param("mindRecordId") Long mindRecordId,
            @Param("receiverId") Long receiverId);

    Optional<MindRecordReceiver> findByMindRecordAndReceiver(MindRecord mindRecord, Receiver receiver);

    boolean existsByMindRecordAndReceiver(MindRecord mindRecord, Receiver receiver);
}
