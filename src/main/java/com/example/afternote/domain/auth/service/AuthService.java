package com.example.afternote.domain.auth.service;


import com.example.afternote.domain.auth.dto.*;
import com.example.afternote.domain.auth.service.social.SocialLoginFactory;
import com.example.afternote.domain.auth.service.social.SocialLoginService;
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
    private final EmailService emailService;
    
    // 🎯 핵심: SocialLoginFactory 주입
    private final SocialLoginFactory socialLoginFactory;

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

        // 소셜 로그인 사용자는 일반 로그인 불가
        if (user.getPassword() == null) {
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_USER);
        }

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
        
        // 3. Redis에서 원자적으로 조회 및 삭제 (TOCTOU 방지)
        // 동시 요청이 들어와도 하나만 성공하도록 보장
        Long storedUserId = tokenService.getUserIdAndDelete(refreshToken);
        if (storedUserId == null || !storedUserId.equals(userId)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        // 4. 신규 토큰 발급 (RTR 전략)
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
        
        // 소셜 로그인 사용자는 비밀번호 변경 불가
        if (user.getPassword() == null) {
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_USER);
        }
        
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

    @Transactional
    public void emailSend(EmailSendRequest request) {
        emailService.sendCode(request.getEmail());
    }

    @Transactional
    public void emailVerify(EmailVerifyRequest request) {
        if(!emailService.verifyCode(request.getEmail(), request.getCertificateCode())){
            throw new CustomException(ErrorCode.INVALID_EMAIL_VERIFICATION);
        }
    }

    /**
     * 🎯 통합 소셜 로그인 (전략 패턴 적용)
     * 
     * 새로운 소셜 로그인 제공자를 추가할 때:
     * 1. SocialLoginService 인터페이스를 구현한 클래스 하나만 만들면 끝!
     * 2. 이 메서드는 1줄도 수정할 필요 없음 (OCP 원칙)
     * 
     * @param request 소셜 로그인 요청 (provider, accessToken)
     * @return JWT 토큰 정보와 신규 회원 여부
     */
    @Transactional
    public SocialLoginResponse socialLogin(SocialLoginRequest request) {

        SocialLoginService socialLoginService = socialLoginFactory.getService(request.getProvider());
        SocialUserInfo socialUserInfo = socialLoginService.getUserInfo(request.getAccessToken());

        User user = userRepository.findByEmail(socialUserInfo.getEmail())
                .orElse(null);
        boolean isNewUser = false;
        
        if (user == null) {
            user = User.builder()
                    .email(socialUserInfo.getEmail())
                    .name(socialUserInfo.getName())
                    .status(UserStatus.ACTIVE)
                    .provider(socialUserInfo.getProvider())
                    .build();
            user = userRepository.save(user);
            isNewUser = true;
        }
        
        // 5. JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        tokenService.saveToken(refreshToken, user.getId());
        
        return SocialLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .build();
    }

}
