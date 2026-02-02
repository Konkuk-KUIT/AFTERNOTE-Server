package com.example.afternote.domain.afternote.service;

import com.example.afternote.domain.afternote.dto.AfternotePageResponse;
import com.example.afternote.domain.afternote.dto.AfternoteResponse;
import com.example.afternote.domain.afternote.model.Afternote;
import com.example.afternote.domain.afternote.model.AfternoteCategoryType;
import com.example.afternote.domain.afternote.repository.AfternoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AfternoteService {

    private final AfternoteRepository afternoteRepository;

    public AfternotePageResponse getAfternotes(Long userId,AfternoteCategoryType category, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Afternote> afternotePage;
        
        if (category != null) {
            afternotePage = afternoteRepository.findByUserIdAndCategoryTypeOrderByCreatedAtDesc(userId, category, pageable);
        } else {
            afternotePage = afternoteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        
        List<AfternoteResponse> content = afternotePage.getContent().stream()
                .map(afternote -> AfternoteResponse.builder()
                        .noteId(afternote.getId())
                        .title(afternote.getTitle())
                        .category(afternote.getCategoryType())
                        .createdAt(afternote.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return AfternotePageResponse.builder()
                .content(content)
                .page(page)
                .size(size)
                .hasNext(afternotePage.hasNext())
                .build();
    }
}
