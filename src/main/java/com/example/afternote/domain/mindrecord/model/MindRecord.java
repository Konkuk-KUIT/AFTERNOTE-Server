package com.example.afternote.domain.mindrecord.model;

import com.example.afternote.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mind_records")
@Getter
@NoArgsConstructor
public class MindRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 기록 타입 (DAILY_QUESTION / DIARY / DEEP_THOUGHT)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MindRecordType type;

    // 리스트, 캘린더에 노출되는 제목
    @Column(nullable = false, length = 100)
    private String title;

    // 기록 날짜 (캘린더 기준 날짜)
    @Column(nullable = false)
    private LocalDate recordDate;

    // 임시저장 여부
    @Column(nullable = false)
    private Boolean isDraft;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 생성 시각 자동 세팅
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // 수정 시각 자동 갱신
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static MindRecord create(
            User user,
            MindRecordType type,
            String title,
            LocalDate recordDate,
            boolean isDraft
    ) {
        MindRecord record = new MindRecord();
        record.user = user;
        record.type = type;
        record.title = title;
        record.recordDate = recordDate;
        record.isDraft = isDraft;
        return record;
    }

    public void updateCommon(
            String title,
            LocalDate recordDate,
            Boolean isDraft
    ) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }

        if (recordDate != null) {
            this.recordDate = recordDate;
        }

        if (isDraft != null) {
            this.isDraft = isDraft;
        }
    }
}