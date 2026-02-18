package com.example.afternote.domain.mindrecord.service;

import com.example.afternote.domain.mindrecord.dto.PatchMindRecordRequest;
import com.example.afternote.domain.mindrecord.dto.PostMindRecordRequest;
import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.thought.model.DeepThought;
import com.example.afternote.domain.mindrecord.thought.repository.DeepThoughtRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeepThoughtService {

    private final DeepThoughtRepository deepThoughtRepository;

    public void create(MindRecord record, PostMindRecordRequest request) {
        if (request.getCategory() == null || request.getCategory().isBlank()) {
            throw new CustomException(ErrorCode.DEEP_THOUGHT_CATEGORY_REQUIRED);
        }
        DeepThought thought =
                DeepThought.create(record, request.getCategory());
        deepThoughtRepository.save(thought);
    }

    public void update(MindRecord record, PatchMindRecordRequest request) {
        DeepThought thought = deepThoughtRepository.findByMindRecord(record)
                .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));

        if (request.getCategory() != null) {
            if (request.getCategory().isBlank()) {
                throw new CustomException(ErrorCode.DEEP_THOUGHT_CATEGORY_REQUIRED);
            }
            thought.updateCategory(request.getCategory());
        }


    }
}