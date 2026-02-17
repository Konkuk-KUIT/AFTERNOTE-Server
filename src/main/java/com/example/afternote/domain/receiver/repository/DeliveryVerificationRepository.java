package com.example.afternote.domain.receiver.repository;

import com.example.afternote.domain.receiver.model.DeliveryVerification;
import com.example.afternote.domain.receiver.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryVerificationRepository extends JpaRepository<DeliveryVerification, Long> {

    Optional<DeliveryVerification> findByUserIdAndReceiverIdAndStatus(Long userId, Long receiverId, VerificationStatus status);

    List<DeliveryVerification> findByStatus(VerificationStatus status);

    List<DeliveryVerification> findByUserId(Long userId);

    List<DeliveryVerification> findByUserIdAndStatus(Long userId, VerificationStatus status);

    Optional<DeliveryVerification> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndReceiverIdAndStatus(Long userId, Long receiverId, VerificationStatus status);
}
