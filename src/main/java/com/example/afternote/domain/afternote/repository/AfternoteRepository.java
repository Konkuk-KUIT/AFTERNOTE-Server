package com.example.afternote.domain.afternote.repository;

import com.example.afternote.domain.afternote.model.Afternote;
import com.example.afternote.domain.afternote.model.AfternoteCategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AfternoteRepository extends JpaRepository<Afternote, Long> {
    
    // 전체 목록 페이징 조회
    Page<Afternote> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 카테고리별 필터링 페이징 조회
    Page<Afternote> findByUserIdAndCategoryTypeOrderByCreatedAtDesc(Long userId, AfternoteCategoryType categoryType, Pageable pageable);
    
    // 해당 사용자의 최대 sortOrder 조회
    @Query("SELECT MAX(a.sortOrder) FROM Afternote a WHERE a.userId = :userId")
    Optional<Integer> findMaxSortOrderByUserId(@Param("userId") Long userId);
}
