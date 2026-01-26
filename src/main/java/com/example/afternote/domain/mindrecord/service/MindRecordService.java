package com.example.afternote.domain.mindrecord.service;

import com.example.afternote.domain.mindrecord.dto.GetMindRecordListRequest;
import com.example.afternote.domain.mindrecord.dto.GetMindRecordListResponse;
import com.example.afternote.domain.mindrecord.dto.MindRecordListItemDto;
import com.example.afternote.domain.mindrecord.dto.PostMindRecordRequest;
import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.model.MindRecordType;
import com.example.afternote.domain.mindrecord.repository.MindRecordRepository;
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

        MindRecord record = switch (request.getType()) {
            case DIARY -> createDiary(user, request);
            case DAILY_QUESTION -> createDailyQuestion(user, request);
            case DEEP_THOUGHT -> createDeepThought(user, request);
        };

        mindRecordRepository.save(record);
        return record.getId();
    }

    private MindRecord createBaseRecord(User user, MindRecordType type, PostMindRecordRequest request ){
        LocalDate recordDate;
        try {
            recordDate = LocalDate.parse(request.getDate());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return MindRecord.create(
                user, type, request.getTitle(), recordDate, request.getIsDraft()
        );
    }


    private MindRecord createDiary(User user, PostMindRecordRequest request) {
        return createBaseRecord(user, MindRecordType.DIARY, request);
    }

    private MindRecord createDailyQuestion(User user, PostMindRecordRequest request) {
        if (request.getQuestionId() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return createBaseRecord(user, MindRecordType.DAILY_QUESTION, request);
    }

    private MindRecord createDeepThought(User user, PostMindRecordRequest request) {
        if (request.getCategory() == null || request.getCategory().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return createBaseRecord(user, MindRecordType.DEEP_THOUGHT, request);
    }

    private void validateCommonFields(PostMindRecordRequest request) {
        if (request.getType() == null ||
                request.getDate() == null ||
                request.getIsDraft() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

}