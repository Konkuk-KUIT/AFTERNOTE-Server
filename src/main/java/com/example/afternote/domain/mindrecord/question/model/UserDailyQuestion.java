package com.example.afternote.domain.mindrecord.question.model;

import com.example.afternote.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_daily_question", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "question_date"})})
@Getter
@NoArgsConstructor
public class UserDailyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private DailyQuestion dailyQuestion;

    @Column(name = "question_date", nullable = false)
    private LocalDate questionDate;

    @Column(nullable = false)
    private Boolean isAnswered = false;

    public static UserDailyQuestion create(
            User user,
            DailyQuestion dailyQuestion,
            LocalDate questionDate
    ) {
        UserDailyQuestion userDailyQuestion = new UserDailyQuestion();
        userDailyQuestion.user = user;
        userDailyQuestion.dailyQuestion = dailyQuestion;
        userDailyQuestion.questionDate = questionDate;
        userDailyQuestion.isAnswered = false;
        return userDailyQuestion;
    }

    public void markAnswered() {
        this.isAnswered = true;
    }
}