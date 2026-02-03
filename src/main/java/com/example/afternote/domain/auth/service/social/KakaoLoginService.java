package com.example.afternote.domain.auth.service.social;

import com.example.afternote.domain.auth.dto.SocialUserInfo;
import com.example.afternote.domain.user.model.AuthProvider;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 카카오 소셜 로그인 구현체
 * 
 * SocialLoginService를 구현하여 카카오 특화 로그인 로직을 처리합니다.
 * 새로운 소셜 로그인을 추가하려면 이 클래스를 참고하여 동일한 구조로 만들면 됩니다!
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService implements SocialLoginService {
    
    private final RestTemplate restTemplate;
    
    // application.yml에서 설정값을 가져옵니다
    // kakao.api.user-info-url: https://kapi.kakao.com/v2/user/me
    @Value("${kakao.api.user-info-url}")
    private String kakaoUserInfoUrl;
    
    @Override
    public SocialUserInfo getUserInfo(String accessToken) {
        try {
            log.debug("🔍 카카오 사용자 정보 조회 시작 - Token: {}...", accessToken.substring(0, Math.min(20, accessToken.length())));
            
            // 1. 카카오 API에 사용자 정보 요청
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            log.debug("📤 카카오 API 호출: {}", kakaoUserInfoUrl);
            ResponseEntity<Map> response = restTemplate.exchange(
                kakaoUserInfoUrl,
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            log.debug("📥 카카오 API 응답 상태: {}", response.getStatusCode());
            
            // 2. 카카오 API 응답 파싱
            Map<String, Object> responseBody = response.getBody();
            log.debug("📦 응답 Body: {}", responseBody);
            
            if (responseBody == null || responseBody.isEmpty()) {
                log.error("❌ 카카오 API 응답이 비어있습니다");
                throw new CustomException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            
            String providerId = String.valueOf(responseBody.get("id"));
            log.debug("👤 Provider ID: {}", providerId);
            
            // kakao_account 객체에서 이메일과 프로필 정보 추출
            Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
            if (kakaoAccount == null) {
                log.error("❌ kakao_account가 응답에 없습니다. 응답: {}", responseBody);
                throw new CustomException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            
            String email = (String) kakaoAccount.get("email");
            log.debug("📧 이메일: {}", email);
            
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile == null) {
                log.error("❌ profile이 kakao_account에 없습니다. kakaoAccount: {}", kakaoAccount);
                throw new CustomException(ErrorCode.SOCIAL_LOGIN_FAILED);
            }
            
            String nickname = (String) profile.get("nickname");
            String profileImageUrl = (String) profile.get("profile_image_url");
            log.debug("✅ 사용자 정보 추출 완료 - 닉네임: {}", nickname);
            
            // 3. 공통 형식인 SocialUserInfo로 변환
            return SocialUserInfo.builder()
                    .providerId(providerId)
                    .email(email)
                    .name(nickname)
                    .provider(AuthProvider.KAKAO)
                    .profileImageUrl(profileImageUrl)
                    .build();
                    
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ 카카오 사용자 정보 조회 실패: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }
    
    @Override
    public String getProviderName() {
        return "KAKAO";
    }
}
