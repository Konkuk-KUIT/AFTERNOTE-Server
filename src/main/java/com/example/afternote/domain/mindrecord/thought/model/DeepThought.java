package com.example.afternote.domain.mindrecord.thought.model;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deep_thought")
@Getter
@NoArgsConstructor
public class DeepThought {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공통 기록
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mind_record_id", nullable = false, unique = true)
    private MindRecord mindRecord;

    // 깊은 생각 카테고리
    @Column(length = 50, nullable = false)
    private String category;

    // 본문
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public static DeepThought create(
            MindRecord mindRecord,
            String category,
            String content
    ) {
        DeepThought record = new DeepThought();
        record.mindRecord = mindRecord;
        record.category = category;
        record.content = content;
        return record;
    }

    public void updateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new CustomException(ErrorCode.DEEP_THOUGHT_CATEGORY_REQUIRED);
        }
        this.category = category;
    }

    public void updateContent(String content) {
        if (content == null) {
            throw new CustomException(ErrorCode.MIND_RECORD_CONTENT_REQUIRED);
        }
        this.content = content;
    }
}