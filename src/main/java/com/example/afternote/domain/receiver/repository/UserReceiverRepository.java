package com.example.afternote.domain.receiver.repository;

import com.example.afternote.domain.receiver.model.UserReceiver;
import com.example.afternote.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserReceiverRepository extends JpaRepository<UserReceiver, Long> {

    // 특정 사용자의 수신인 관계 전체 조회 (Receiver fetch join)
    @Query("""
        select ur
        from UserReceiver ur
        join fetch ur.receiver
        where ur.user = :user
    """)
    List<UserReceiver> findAllByUser(User user);

    // 특정 사용자의 특정 수신인 관계 조회
    Optional<UserReceiver> findByUserAndReceiverId(User user, Long receiverId);
}