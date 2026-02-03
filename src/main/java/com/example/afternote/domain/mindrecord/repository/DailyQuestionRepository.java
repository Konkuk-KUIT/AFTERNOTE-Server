package com.example.afternote.domain.mindrecord.repository;

import com.example.afternote.domain.mindrecord.question.model.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long> {

    // 아직 이 유저에게 한 번도 노출되지 않은 질문
    @Query("""
    select q from DailyQuestion q
    where q.isActive = true
      and not exists (
          select 1
          from UserDailyQuestion udq
          where udq.dailyQuestion = q
            and udq.user.id = :userId
      )
    order by function('RAND')
""")
    // 해당 사용자에게 한번도 노출되지 않은 질문 중 하나를 랜덤으로 조회
    List<DailyQuestion> findRandomUnexposedQuestion(Long userId);

    // 과거에 노출된 질문 (fallback용)
    @Query("""
    select q from DailyQuestion q
    where q.isActive = true
      and exists (
          select 1
          from UserDailyQuestion udq
          where udq.dailyQuestion = q
            and udq.user.id = :userId
      )
    order by function('RAND')
""")
    // 이미 사용자에게 노출된 적이 있는 질문 중 하나를 랜덤으로 조회 (신규 질문 모두 소진될 때)
    List<DailyQuestion> findRandomExposedQuestion(Long userId);
}