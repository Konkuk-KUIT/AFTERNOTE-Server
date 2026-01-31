package com.example.afternote.domain.mindrecord.question.repository;

import com.example.afternote.domain.mindrecord.question.model.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long> {
}