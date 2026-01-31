package com.example.afternote.domain.mindrecord.service;

import com.example.afternote.domain.mindrecord.diary.model.Diary;
import com.example.afternote.domain.mindrecord.diary.repository.DiaryRepository;
import com.example.afternote.domain.mindrecord.dto.*;
import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.model.MindRecordType;
import com.example.afternote.domain.mindrecord.question.model.DailyQuestion;
import com.example.afternote.domain.mindrecord.question.model.DailyQuestionAnswer;
import com.example.afternote.domain.mindrecord.question.repository.DailyQuestionAnswerRepository;
import com.example.afternote.domain.mindrecord.question.repository.DailyQuestionRepository;
import com.example.afternote.domain.mindrecord.repository.MindRecordRepository;
import com.example.afternote.domain.mindrecord.thought.model.DeepThought;
import com.example.afternote.domain.mindrecord.thought.repository.DeepThoughtRepository;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MindRecordService {

    private final MindRecordRepository mindRecordRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DailyQuestionAnswerRepository dailyQuestionAnswerRepository;
    private final DeepThoughtRepository deepThoughtRepository;
    private final DailyQuestionRepository dailyQuestionRepository;

    /**
     * 마음의 기록 목록 조회 (LIST / CALENDAR 공통)
     */
    public GetMindRecordListResponse getMindRecordList(
            Long userId,
            GetMindRecordListRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<MindRecord> records = request.isCalendarView()
                ? findCalendarRecords(user, request)
                : findListRecords(user, request.getType());

        return new GetMindRecordListResponse(
                convertToDtos(records),
                createMarkedDates(records, request.isCalendarView())
        );
    }

    /* ================= 조회 ================= */

    private List<MindRecord> findCalendarRecords(
            User user, GetMindRecordListRequest request
    ) {
        if (request.getYear() == null || request.getMonth() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        MindRecordType type = request.getType();
        YearMonth yearMonth;

        try {
            yearMonth = YearMonth.of(request.getYear(), request.getMonth());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return mindRecordRepository.findByUserAndTypeAndRecordDateBetween(
                user, type, yearMonth.atDay(1), yearMonth.atEndOfMonth()
        );
    }

    private List<MindRecord> findListRecords(
            User user, MindRecordType type
    ) {
        return mindRecordRepository.findByUserAndType(user, type);
    }

    /* ================= 가공 ================= */

    private List<MindRecordListItemDto> convertToDtos(
            List<MindRecord> records
    ) {
        return records.stream()
                .map(MindRecordListItemDto::from)
                .toList();
    }

    private List<String> createMarkedDates(
            List<MindRecord> records, boolean isCalendarView
    ) {
        if (!isCalendarView) {
            return null;
        }
        return records.stream()
                .map(record -> record.getRecordDate().toString())
                .distinct()
                .toList();
    }

    /**
     * 마음의 기록 생성 (POST)
     */
    @Transactional
    public Long createMindRecord(
            Long userId,
            PostMindRecordRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        validateCommonFields(request);  // 공통 필드 검증

        LocalDate recordDate;
        try {
            recordDate = LocalDate.parse(request.getDate());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        MindRecord record = switch (request.getType()) {
            case DIARY -> MindRecord.diary(user, request.getTitle(), recordDate, request.getIsDraft());
            case DAILY_QUESTION -> MindRecord.dailyQuestion(user, request.getTitle(), recordDate, request.getIsDraft());
            case DEEP_THOUGHT -> MindRecord.deepThought(user, request.getTitle(), recordDate, request.getIsDraft());
        };

        mindRecordRepository.save(record);

        switch (request.getType()) {
            case DIARY -> {
                Diary diary = Diary.create(record, request.getContent());
                diaryRepository.save(diary);
            }
            case DAILY_QUESTION -> {
                DailyQuestion question = dailyQuestionRepository.findById(request.getQuestionId())
                        .orElseThrow(() -> new CustomException(ErrorCode.DAILY_QUESTION_NOT_FOUND));

                DailyQuestionAnswer answer =
                        DailyQuestionAnswer.create(record, question, request.getContent());
                dailyQuestionAnswerRepository.save(answer);
            }
            case DEEP_THOUGHT -> {
                DeepThought thought =
                        DeepThought.create(record, request.getCategory(), request.getContent());
                deepThoughtRepository.save(thought);
            }
        }
        return record.getId();
    }

    private void validateCommonFields(PostMindRecordRequest request) {
        if (request.getType() == null ||
                request.getDate() == null ||
                request.getIsDraft() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    /**
     * 마음의 기록 단건 수정 화면 조회
     */
    /* ================= 단건 조회 ================= */

    public GetMindRecordDetailResponse getMindRecordDetail(Long userId, Long recordId) {
        MindRecord record = mindRecordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.MIND_RECORD_FORBIDDEN);
        }

        return switch (record.getType()) {
            case DIARY -> {
                Diary diary = diaryRepository.findByMindRecord(record)
                        .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));
                yield GetMindRecordDetailResponse.from(record, diary);
            }

            case DAILY_QUESTION -> {
                DailyQuestionAnswer answer = dailyQuestionAnswerRepository.findByMindRecord(record)
                        .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));
                yield GetMindRecordDetailResponse.from(record, answer);
            }

            case DEEP_THOUGHT -> {
                DeepThought thought = deepThoughtRepository.findByMindRecord(record)
                        .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));
                yield GetMindRecordDetailResponse.from(record, thought);
            }
        };
    }

}