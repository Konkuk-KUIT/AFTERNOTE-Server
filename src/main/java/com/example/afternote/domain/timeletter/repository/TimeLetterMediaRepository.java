package com.example.afternote.domain.timeletter.repository;

import com.example.afternote.domain.timeletter.model.TimeLetterMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeLetterMediaRepository extends JpaRepository<TimeLetterMedia, Long> {

    List<TimeLetterMedia> findByTimeLetterId(Long timeLetterId);

    List<TimeLetterMedia> findByTimeLetterIdIn(List<Long> timeLetterIds);

    void deleteByTimeLetterId(Long timeLetterId);

    void deleteByTimeLetterIdIn(List<Long> timeLetterIds);
}
