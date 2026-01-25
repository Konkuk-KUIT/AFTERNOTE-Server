package com.example.afternote.domain.user.service;

import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.repository.ReceivedRepository;
import com.example.afternote.domain.user.dto.*;
import com.example.afternote.domain.user.model.User;
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
public class UserService {
    private final UserRepository userRepository;
    private final ReceivedRepository receiverRepository;

    public UserResponse getMyProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateMyProfile(Long userId, UserUpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(
                request.getName(),
                request.getPhone(),
                request.getProfileImageUrl()
        );

        return UserResponse.from(user);
    }


    public UserPushSettingResponse getMyPushSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserPushSettingResponse.from(user);
    }

    @Transactional
    public UserPushSettingResponse updateMyPushSettings(Long userId, UserUpdatePushSettingRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updatePushSettings(
                request.getTimeLetter(),
                request.getMindRecord(),
                request.getAfterNote()
        );

        return UserPushSettingResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<ReceiverDetailResponse> getReceivers(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return receiverRepository.findAllByUser(user).stream()
                .map(ReceiverDetailResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReceiverDetailResponse getReceiverDetail(Long userId, Long receiverId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Receiver receiver = receiverRepository.findByIdAndUser(receiverId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ReceiverDetailResponse.from(receiver);
    }


}
