package com.example.afternote.domain.mindrecord.service;

import com.example.afternote.domain.mindrecord.dto.GetDailyQuestionResponse;
import com.example.afternote.domain.mindrecord.question.model.DailyQuestion;
import com.example.afternote.domain.mindrecord.question.model.UserDailyQuestion;
import com.example.afternote.domain.mindrecord.repository.DailyQuestionRepository;
import com.example.afternote.domain.mindrecord.repository.UserDailyQuestionRepository;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyQuestionService {

    private final DailyQuestionRepository dailyQuestionRepository;
    private final UserDailyQuestionRepository userDailyQuestionRepository;
    private final UserRepository userRepository;

    @Transactional
    public GetDailyQuestionResponse getTodayDailyQuestion(Long userId) {
        LocalDate today = LocalDate.now();

        UserDailyQuestion userDailyQuestion =
                userDailyQuestionRepository
                        .findByUserIdAndQuestionDate(userId, today)
                        .orElseGet(() -> createTodayQuestion(userId, today));

        DailyQuestion question = userDailyQuestion.getDailyQuestion();

        return new GetDailyQuestionResponse(
                question.getId(),
                question.getContent()
        );
    }

    private UserDailyQuestion createTodayQuestion(Long userId, LocalDate today) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        DailyQuestion question =
                dailyQuestionRepository
                        .findRandomUnexposedQuestion(userId)
                        .stream()
                        .findFirst()
                        .orElseGet(() ->
                                dailyQuestionRepository
                                        .findRandomExposedQuestion(userId)
                                        .stream()
                                        .findFirst()
                                        .orElseThrow() // TODO: DAILY_QUESTION_NOT_FOUND
                        );

        UserDailyQuestion userDailyQuestion =
                UserDailyQuestion.create(user, question, today);

        return userDailyQuestionRepository.save(userDailyQuestion);
    }

}
