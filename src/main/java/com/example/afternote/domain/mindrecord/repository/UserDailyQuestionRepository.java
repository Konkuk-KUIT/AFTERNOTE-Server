package com.example.afternote.domain.mindrecord.repository;

import com.example.afternote.domain.mindrecord.question.model.UserDailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserDailyQuestionRepository
        extends JpaRepository<UserDailyQuestion, Long> {

    // 사용자가 해당 날짜에 이미 질문 받은 적 있는지 조회 (1일 1질문 보장)
    Optional<UserDailyQuestion> findByUserIdAndQuestionDate(
            Long userId,
            LocalDate questionDate
    );
}