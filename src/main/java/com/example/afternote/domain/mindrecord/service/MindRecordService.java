package com.example.afternote.domain.mindrecord.service;

import com.example.afternote.domain.mindrecord.dto.GetMindRecordListRequest;
import com.example.afternote.domain.mindrecord.dto.GetMindRecordListResponse;
import com.example.afternote.domain.mindrecord.dto.MindRecordListItemDto;
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
        YearMonth yearMonth = YearMonth.of(request.getYear(), request.getMonth());

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
}