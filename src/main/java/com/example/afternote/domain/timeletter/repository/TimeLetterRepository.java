package com.example.afternote.domain.timeletter.repository;

import com.example.afternote.domain.timeletter.model.TimeLetter;
import com.example.afternote.domain.timeletter.model.TimeLetterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeLetterRepository extends JpaRepository<TimeLetter, Long> {

    List<TimeLetter> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, TimeLetterStatus status);

    Optional<TimeLetter> findByIdAndUserId(Long id, Long userId);

    List<TimeLetter> findByIdInAndUserId(List<Long> ids, Long userId);

    void deleteByUserIdAndStatus(Long userId, TimeLetterStatus status);
}
