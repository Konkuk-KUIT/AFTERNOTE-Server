package com.example.afternote.domain.user.service;

import com.example.afternote.domain.image.service.S3Service;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.model.UserReceiver;
import com.example.afternote.domain.receiver.service.DeliveryVerificationService;
import com.example.afternote.domain.receiver.repository.ReceiverRepository;
import com.example.afternote.domain.receiver.repository.UserReceiverRepository;
import com.example.afternote.domain.user.dto.*;
import com.example.afternote.domain.user.model.DeliveryConditionType;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.domain.receiver.service.AuthCodeMessageService;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserReceiverRepository userReceiverRepository;
    private final ReceiverRepository receiverRepository;
    private final AuthCodeMessageService authCodeMessageService;
    private final S3Service s3Service;
    private final DeliveryVerificationService deliveryVerificationService;

    public UserResponse getMyProfile(Long userId) {

        User user = findUserById(userId);
        return UserResponse.from(user, s3Service::generateGetPresignedUrl);
    }

    @Transactional
    public UserResponse updateMyProfile(Long userId, UserUpdateProfileRequest request) {

        User user = findUserById(userId);

        user.updateProfile(
                request.getName(),
                request.getPhone(),
                request.getProfileImageUrl()
        );

        return UserResponse.from(user, s3Service::generateGetPresignedUrl);
    }


    public UserPushSettingResponse getMyPushSettings(Long userId) {
        User user = findUserById(userId);

        return UserPushSettingResponse.from(user);
    }

    @Transactional
    public UserPushSettingResponse updateMyPushSettings(Long userId, UserUpdatePushSettingRequest request
    ) {
        User user = findUserById(userId);

        user.updatePushSettings(
                request.getTimeLetter(),
                request.getMindRecord(),
                request.getAfterNote()
        );

        return UserPushSettingResponse.from(user);
    }

    public List<ReceiverListResponse> getReceivers(Long userId) {

        User user = findUserById(userId);

        return userReceiverRepository.findAllByUser(user).stream()
                .map(ur -> ReceiverListResponse.from(ur.getReceiver()))
                .toList();
    }

    public ReceiverDetailResponse getReceiverDetail(Long userId, Long receiverId) {

        User user = findUserById(userId);

        UserReceiver userReceiver =
                userReceiverRepository.findByUserAndReceiverId(user, receiverId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Receiver receiver = userReceiver.getReceiver();

        // TODO: DailyQuestion, TimeLetter, AfterNote 도메인 생성 후 count 로직 추가
        int dailyCount = 0;
        int timeLetterCount = 0;
        int afterNoteCount = 0;

        return ReceiverDetailResponse.from(
                receiver,
                dailyCount,
                timeLetterCount,
                afterNoteCount
        );
    }

    public DeliveryConditionResponse getDeliveryCondition(Long userId) {
        User user = findUserById(userId);
        return DeliveryConditionResponse.from(user);
    }

    @Transactional
    public DeliveryConditionResponse updateDeliveryCondition(Long userId, DeliveryConditionRequest request) {
        User user = findUserById(userId);
        DeliveryConditionType previousConditionType = user.getDeliveryConditionType();

        user.updateDeliveryCondition(
                request.getConditionType(),
                request.getInactivityPeriodDays(),
                request.getSpecificDate()
        );

        if (previousConditionType == DeliveryConditionType.DEATH_CERTIFICATE
                && user.getDeliveryConditionType() != DeliveryConditionType.DEATH_CERTIFICATE) {
            deliveryVerificationService.cancelPendingVerifications(user.getId());
        }

        return DeliveryConditionResponse.from(user);
    }

    @Transactional
    public UserCreateReceiverResponse createReceiver(
            Long userId,
            UserCreateReceiverRequest request
    ) {
        User user = findUserById(userId);

        Receiver receiver = Receiver.builder()
                .name(request.getName())
                .relation(request.getRelation())
                .phone(request.getPhone())
                .email(request.getEmail())
                .userId(user.getId())
                .build();

        receiverRepository.save(receiver);

        UserReceiver userReceiver = UserReceiver.builder()
                .user(user)
                .receiver(receiver)
                .build();

        userReceiverRepository.save(userReceiver);

        if (receiver.getEmail() != null && !receiver.getEmail().isBlank()) {
            try {
                authCodeMessageService.sendAuthCode(
                        receiver.getEmail(),
                        receiver.getAuthCode(),
                        user.getName(),
                        receiver.getName()
                );
            } catch (Exception e) {
                log.warn("Failed to send auth code via email for receiver {}: {}", receiver.getId(), e.getMessage());
            }
        }

        return UserCreateReceiverResponse.from(receiver.getId(), receiver.getAuthCode());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
