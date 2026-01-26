package com.example.afternote.domain.mindrecord.repository;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MindRecordRepository extends JpaRepository<MindRecord, Long> {

    // 날짜 범위 조회 (CALENDAR)
    List<MindRecord> findByUserAndRecordDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    // 날짜 조건 없는 전체 조회 (LIST 기본)
    List<MindRecord> findByUser(User user);
}