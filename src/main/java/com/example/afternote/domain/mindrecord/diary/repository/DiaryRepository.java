package com.example.afternote.domain.mindrecord.diary.repository;

import com.example.afternote.domain.mindrecord.diary.model.Diary;
import com.example.afternote.domain.mindrecord.model.MindRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByMindRecord(MindRecord mindRecord);

    @Modifying
    void deleteByMindRecord(MindRecord mindRecord);
}