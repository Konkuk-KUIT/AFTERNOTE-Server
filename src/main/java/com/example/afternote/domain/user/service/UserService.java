package com.example.afternote.domain.user.service;

import com.example.afternote.domain.user.dto.UserResponse;
import com.example.afternote.domain.user.dto.UserUpdateProfileRequest;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

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

}
