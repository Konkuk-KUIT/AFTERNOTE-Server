package com.example.afternote.domain.mindrecord.question.model;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "daily_question_answers")
@Getter
@NoArgsConstructor
public class DailyQuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 답변이 대응되는 공통 기록
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mind_record_id", nullable = false, unique = true)
    private MindRecord mindRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private DailyQuestion dailyQuestion;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public static DailyQuestionAnswer create(
            MindRecord mindRecord,
            DailyQuestion dailyQuestion,
            String content
    ) {
        DailyQuestionAnswer answer = new DailyQuestionAnswer();
        answer.mindRecord = mindRecord;
        answer.dailyQuestion = dailyQuestion;
        answer.content = content;
        return answer;
    }
}