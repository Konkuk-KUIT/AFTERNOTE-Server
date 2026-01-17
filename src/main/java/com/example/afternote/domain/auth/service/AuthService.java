package com.example.afternote.domain.auth.service;


import com.example.afternote.domain.auth.dto.*;
import com.example.afternote.domain.user.model.AuthProvider;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.model.UserStatus;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import com.example.afternote.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;


    @Transactional
    public User signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .status(UserStatus.ACTIVE)
                .provider(AuthProvider.LOCAL)
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        tokenService.saveToken(refreshToken, user.getId());

        return LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    @Transactional
    public ReissueResponse reissue(ReissueRequest request) {
        String refreshToken = request.getRefreshToken();
        Long userId = tokenService.getUserId(request.getRefreshToken());
        if (!jwtTokenProvider.validateToken(refreshToken)||userId == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        tokenService.deleteToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        tokenService.saveToken(newRefreshToken, userId);

        return ReissueResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build();

    }
}
