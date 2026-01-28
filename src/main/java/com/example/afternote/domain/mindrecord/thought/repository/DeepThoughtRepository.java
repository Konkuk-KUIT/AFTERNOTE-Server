package com.example.afternote.domain.mindrecord.thought.repository;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.thought.model.DeepThought;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeepThoughtRepository extends JpaRepository<DeepThought, Long> {

    Optional<DeepThought> findByMindRecord(MindRecord mindRecord);
}