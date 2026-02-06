package com.example.afternote.domain.receiver.repository;

import com.example.afternote.domain.receiver.model.MindRecordReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
