package com.example.afternote.domain.receiver.service;

import com.example.afternote.domain.receiver.dto.*;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.repository.ReceiverRepository;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceiverAuthService {

    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    private final ReceiverRepository receiverRepository;
    private final UserRepository userRepository;
    private final ReceivedService receivedService;
    private final DeliveryVerificationService deliveryVerificationService;

    public ReceiverAuthVerifyResponse verifyAuthCode(String authCode) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        User sender = userRepository.findById(receiver.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_CODE));
        return ReceiverAuthVerifyResponse.from(receiver, sender.getName());
    }

    public ReceivedTimeLetterListResponse getTimeLettersByAuthCode(String authCode) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        validateDeliveryCondition(receiver);
        return receivedService.getTimeLetters(receiver.getId());
    }

    public ReceivedAfternoteListResponse getAfternotesByAuthCode(String authCode) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        validateDeliveryCondition(receiver);
        return receivedService.getAfternotes(receiver.getId());
    }

    public ReceivedMindRecordListResponse getMindRecordsByAuthCode(String authCode) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        validateDeliveryCondition(receiver);
        return receivedService.getMindRecords(receiver.getId());
    }

    @Transactional
    public ReceivedTimeLetterResponse getTimeLetterByAuthCode(String authCode, Long timeLetterReceiverId) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        validateDeliveryCondition(receiver);
        return receivedService.getTimeLetter(receiver.getId(), timeLetterReceiverId);
    }

    public ReceivedMindRecordDetailResponse getMindRecordByAuthCode(String authCode, Long mindRecordId) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        validateDeliveryCondition(receiver);
        return receivedService.getMindRecord(receiver.getId(), mindRecordId);
    }

    public ReceivedAfternoteDetailResponse getAfternoteByAuthCode(String authCode, Long afternoteId) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        validateDeliveryCondition(receiver);
        return receivedService.getAfternote(receiver.getId(), afternoteId);
    }

    @Transactional
    public DeliveryVerificationResponse submitDeliveryVerification(String authCode, DeliveryVerificationRequest request) {
        findReceiverByAuthCode(authCode);
        return DeliveryVerificationResponse.from(
                deliveryVerificationService.submitVerification(
                        authCode,
                        request.getDeathCertificateUrl(),
                        request.getFamilyRelationCertificateUrl()
                )
        );
    }

    public ReceiverMessageResponse getMessageByAuthCode(String authCode) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        User sender = userRepository.findById(receiver.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new ReceiverMessageResponse(sender.getName(), receiver.getMessage());
    }

    public DeliveryVerificationResponse getDeliveryVerificationStatus(String authCode) {
        findReceiverByAuthCode(authCode);
        return DeliveryVerificationResponse.from(
                deliveryVerificationService.getVerificationStatus(authCode)
        );
    }

    private Receiver findReceiverByAuthCode(String authCode) {
        if (authCode == null || !UUID_PATTERN.matcher(authCode.toLowerCase()).matches()) {
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        }
        return receiverRepository.findByAuthCode(authCode.toLowerCase())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_CODE));
    }

    private void validateDeliveryCondition(Receiver receiver) {
        User sender = userRepository.findById(receiver.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!sender.isDeliveryConditionMet()) {
            throw new CustomException(ErrorCode.DELIVERY_CONDITION_NOT_MET);
        }
    }
}
