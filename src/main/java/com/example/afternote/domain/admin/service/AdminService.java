package com.example.afternote.domain.admin.service;

import com.example.afternote.domain.admin.dto.AdminVerificationResponse;
import com.example.afternote.domain.image.service.S3Service;
import com.example.afternote.domain.receiver.model.DeliveryVerification;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.repository.ReceiverRepository;
import com.example.afternote.domain.receiver.service.DeliveryVerificationService;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.model.UserRole;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final ReceiverRepository receiverRepository;
    private final DeliveryVerificationService deliveryVerificationService;
    private final S3Service s3Service;

    public void validateAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (user.getRole() != UserRole.ADMIN) {
            throw new CustomException(ErrorCode.ADMIN_REQUIRED);
        }
    }

    public List<AdminVerificationResponse> getPendingVerifications(Long userId) {
        validateAdmin(userId);
        List<DeliveryVerification> verifications = deliveryVerificationService.getPendingVerifications();
        return verifications.stream()
                .map(this::toAdminVerificationResponse)
                .toList();
    }

    public AdminVerificationResponse getVerificationDetail(Long userId, Long verificationId) {
        validateAdmin(userId);
        DeliveryVerification verification = deliveryVerificationService.getVerificationDetail(verificationId);
        return toAdminVerificationResponse(verification);
    }

    @Transactional
    public AdminVerificationResponse approveVerification(Long userId, Long verificationId, String adminNote) {
        validateAdmin(userId);
        DeliveryVerification verification = deliveryVerificationService.approveVerification(verificationId, adminNote);
        return toAdminVerificationResponse(verification);
    }

    @Transactional
    public AdminVerificationResponse rejectVerification(Long userId, Long verificationId, String adminNote) {
        validateAdmin(userId);
        DeliveryVerification verification = deliveryVerificationService.rejectVerification(verificationId, adminNote);
        return toAdminVerificationResponse(verification);
    }

    private AdminVerificationResponse toAdminVerificationResponse(DeliveryVerification verification) {
        User sender = userRepository.findById(verification.getUserId()).orElse(null);
        Receiver receiver = receiverRepository.findById(verification.getReceiverId()).orElse(null);

        String senderName = sender != null ? sender.getName() : "알 수 없음";
        String senderEmail = sender != null ? sender.getEmail() : "알 수 없음";
        String receiverName = receiver != null ? receiver.getName() : "알 수 없음";

        String deathCertPresignedUrl = s3Service.generateGetPresignedUrl(verification.getDeathCertificateUrl());
        String familyRelationCertPresignedUrl = s3Service.generateGetPresignedUrl(verification.getFamilyRelationCertificateUrl());

        return AdminVerificationResponse.from(
                verification, senderName, senderEmail, receiverName,
                deathCertPresignedUrl, familyRelationCertPresignedUrl
        );
    }
}
