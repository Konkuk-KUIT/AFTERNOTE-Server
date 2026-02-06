package com.example.afternote.domain.receiver.repository;

import com.example.afternote.domain.receiver.model.TimeLetterReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeLetterReceiverRepository extends JpaRepository<TimeLetterReceiver, Long> {

    @Query("""
            SELECT tlr FROM TimeLetterReceiver tlr
            JOIN FETCH tlr.timeLetter tl
            JOIN FETCH tl.user
            WHERE tlr.receiver.id = :receiverId
            ORDER BY tlr.createdAt DESC
            """)
    List<TimeLetterReceiver> findByReceiverIdWithTimeLetter(@Param("receiverId") Long receiverId);
}
