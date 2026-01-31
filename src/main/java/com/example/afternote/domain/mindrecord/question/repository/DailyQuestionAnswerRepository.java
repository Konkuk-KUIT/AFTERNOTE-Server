package com.example.afternote.domain.mindrecord.question.repository;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.question.model.DailyQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyQuestionAnswerRepository
        extends JpaRepository<DailyQuestionAnswer, Long> {

    Optional<DailyQuestionAnswer> findByMindRecord(MindRecord mindRecord);

    void deleteByMindRecord(MindRecord mindRecord);
}