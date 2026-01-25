package com.example.afternote.domain.receiver.repository;

import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceivedRepository extends JpaRepository<Receiver, Long> {
    // 특정 사용자가 등록한 모든 수신인 조회
    List<Receiver> findAllByUser(User user);

    // 특정 사용자가 소유한 수신인 단건 조회 (소유자 검증 포함)
    Optional<Receiver> findByIdAndUser(Long receiverId, User user);
}
