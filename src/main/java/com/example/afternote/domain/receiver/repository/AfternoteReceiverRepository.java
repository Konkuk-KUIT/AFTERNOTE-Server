package com.example.afternote.domain.receiver.repository;

import com.example.afternote.domain.afternote.model.AfternoteReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AfternoteReceiverRepository extends JpaRepository<AfternoteReceiver, Long> {

    @Query("""
            SELECT ar FROM AfternoteReceiver ar
            JOIN FETCH ar.afternote a
            WHERE ar.receiver.id = :receiverId
            ORDER BY a.createdAt DESC
            """)
    List<AfternoteReceiver> findByReceiverIdWithAfternote(@Param("receiverId") Long receiverId);
}
