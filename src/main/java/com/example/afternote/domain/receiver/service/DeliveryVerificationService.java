package com.example.afternote.domain.receiver.service;

import com.example.afternote.domain.image.service.S3Service;
import com.example.afternote.domain.receiver.model.DeliveryVerification;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.model.VerificationStatus;
import com.example.afternote.domain.receiver.repository.DeliveryVerificationRepository;
import com.example.afternote.domain.receiver.repository.ReceiverRepository;
import com.example.afternote.domain.user.model.DeliveryConditionType;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryVerificationService {

    private final DeliveryVerificationRepository deliveryVerificationRepository;
    private final ReceiverRepository receiverRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.region}")
    private String region;

    @Transactional
    public DeliveryVerification submitVerification(String authCode, String deathCertUrl, String familyRelationCertUrl) {
        Receiver receiver = receiverRepository.findByAuthCode(authCode)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_CODE));

        User user = userRepository.findById(receiver.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeliveryConditionType() != DeliveryConditionType.DEATH_CERTIFICATE) {
            throw new CustomException(ErrorCode.CONDITION_TYPE_MISMATCH);
        }

        // Validate document URLs
        String s3Prefix = String.format("https://%s.s3.%s.amazonaws.com/documents/", bucket, region);
        if (!deathCertUrl.startsWith(s3Prefix) || !familyRelationCertUrl.startsWith(s3Prefix)) {
            throw new CustomException(ErrorCode.INVALID_DELIVERY_CONDITION);
        }

        if (deliveryVerificationRepository.existsByUserIdAndReceiverIdAndStatus(
                user.getId(), receiver.getId(), VerificationStatus.PENDING)) {
            throw new CustomException(ErrorCode.VERIFICATION_ALREADY_SUBMITTED);
        }

        DeliveryVerification verification = DeliveryVerification.builder()
                .userId(user.getId())
                .receiverId(receiver.getId())
                .deathCertificateUrl(deathCertUrl)
                .familyRelationCertificateUrl(familyRelationCertUrl)
                .build();

        return deliveryVerificationRepository.save(verification);
    }

    @Transactional
    public void cancelPendingVerifications(Long userId) {
        List<DeliveryVerification> pendingVerifications =
                deliveryVerificationRepository.findByUserIdAndStatus(userId, VerificationStatus.PENDING);

        for (DeliveryVerification verification : pendingVerifications) {
            verification.reject("조건 변경으로 자동 취소");
        }
    }

    public DeliveryVerification getVerificationStatus(String authCode) {
        Receiver receiver = receiverRepository.findByAuthCode(authCode)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_CODE));

        User user = userRepository.findById(receiver.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return deliveryVerificationRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.VERIFICATION_NOT_FOUND));
    }

    public List<DeliveryVerification> getPendingVerifications() {
        return deliveryVerificationRepository.findByStatus(VerificationStatus.PENDING);
    }

    public DeliveryVerification getVerificationDetail(Long verificationId) {
        return deliveryVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.VERIFICATION_NOT_FOUND));
    }

    @Transactional
    public DeliveryVerification approveVerification(Long verificationId, String adminNote) {
        DeliveryVerification verification = deliveryVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.VERIFICATION_NOT_FOUND));

        if (verification.getStatus() != VerificationStatus.PENDING) {
            throw new CustomException(ErrorCode.VERIFICATION_ALREADY_PROCESSED);
        }

        verification.approve(adminNote);

        User user = userRepository.findById(verification.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.fulfillCondition();

        return verification;
    }

    @Transactional
    public DeliveryVerification rejectVerification(Long verificationId, String adminNote) {
        DeliveryVerification verification = deliveryVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.VERIFICATION_NOT_FOUND));

        if (verification.getStatus() != VerificationStatus.PENDING) {
            throw new CustomException(ErrorCode.VERIFICATION_ALREADY_PROCESSED);
        }

        verification.reject(adminNote);

        return verification;
    }
}
