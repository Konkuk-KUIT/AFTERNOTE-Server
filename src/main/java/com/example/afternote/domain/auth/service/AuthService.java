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
        
        // 1. JWT 자체의 유효성 먼저 검증 (서명, 만료시간)
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        // 2. JWT에서 userId 추출
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        
        // 3. Redis에 저장된 userId와 비교 (토큰이 탈취되지 않았는지 확인)
        Long storedUserId = tokenService.getUserId(refreshToken);
        if (storedUserId == null || !storedUserId.equals(userId)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        // 4. 기존 토큰 삭제 후 신규 발급 (RTR 전략)
        tokenService.deleteToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        tokenService.saveToken(newRefreshToken, userId);

        return ReissueResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void passwordChange(Long userId,PasswordChangeRequest request) {
        if(request.getNewPassword().equals(request.getCurrentPassword())){
            throw new CustomException(ErrorCode.NEWPASSWORD_MATCH);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH); // 현재 비번 틀림 예외
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void logout(Long userId, LogoutRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // Redis에서 조회한 userId와 JWT의 userId가 일치하는지 확인
        Long storedUserId = tokenService.getUserId(refreshToken);
        if (storedUserId == null || !storedUserId.equals(userId)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        // Refresh Token 삭제 (이후 재발급 불가)
        tokenService.deleteToken(refreshToken);
    }
}
