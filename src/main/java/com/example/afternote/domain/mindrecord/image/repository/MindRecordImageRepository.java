package com.example.afternote.domain.mindrecord.image.repository;

import com.example.afternote.domain.mindrecord.image.model.MindRecordImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MindRecordImageRepository extends JpaRepository<MindRecordImage, Long> {

    List<MindRecordImage> findByMindRecordIdOrderByIdAsc(Long mindRecordId);

    void deleteByMindRecordId(Long mindRecordId);
}
