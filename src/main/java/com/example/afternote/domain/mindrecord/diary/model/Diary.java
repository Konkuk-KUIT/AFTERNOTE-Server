package com.example.afternote.domain.mindrecord.diary.model;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary")
@Getter
@NoArgsConstructor
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공통 기록
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mind_record_id", nullable = false, unique = true)
    private MindRecord mindRecord;

    // 일기 본문
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public static Diary create(MindRecord mindRecord, String content) {
        Diary record = new Diary();
        record.mindRecord = mindRecord;
        record.content = content;
        return record;
    }

    public void updateContent(String content) {
        if (content == null) {
            throw new CustomException(ErrorCode.MIND_RECORD_CONTENT_REQUIRED);
        }
        this.content = content;
    }
}