package com.example.afternote.domain.user.repository;

import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.model.UserStatus;
import com.example.afternote.domain.user.model.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);
    
    // 이메일 중복 체크
    boolean existsByEmail(String email);
    
    // 이메일과 프로바이더로 사용자 조회 (소셜 로그인용)
    Optional<User> findByEmailAndProvider(String email, AuthProvider provider);
    
    // 상태로 사용자 목록 조회
    List<User> findByStatus(UserStatus status);
    
    // 프로바이더로 사용자 목록 조회
    List<User> findByProvider(AuthProvider provider);
    
    // 이름으로 사용자 검색 (부분 일치)
    List<User> findByNameContaining(String name);
    
}
