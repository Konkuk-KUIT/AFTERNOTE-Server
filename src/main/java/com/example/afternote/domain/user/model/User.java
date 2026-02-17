package com.example.afternote.domain.user.model;

import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users") // DB 예약어 방지를 위해 테이블명은 users 권장
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간 자동 관리
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // 이메일은 중복 방지
    private String email;

    @Column // 소셜 로그인 사용자는 비밀번호가 없을 수 있음
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column
    private String phone; // 연락처 (선택)

    @Column(length = 1000)
    private String profileImageUrl; // 프로필 이미지 URL (선택)

    @Enumerated(EnumType.STRING) // DB에 숫자가 아닌 텍스트(ACTIVE)로 저장
    @Column(nullable = false)
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProvider> providers = new HashSet<>(); // 연결된 소셜 로그인 제공자 목록

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryConditionType deliveryConditionType;

    @Column
    private Integer inactivityPeriodDays;

    @Column
    private LocalDate specificDate;

    @Column(nullable = false)
    private boolean conditionFulfilled;

    @Column(nullable = false)
    private boolean timeLetterPushEnabled;

    @Column(nullable = false)
    private boolean mindRecordPushEnabled;

    @Column(nullable = false)
    private boolean afterNotePushEnabled;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    @Builder
    public User(String email, String password, String name, String phone,
                String profileImageUrl, UserStatus status, AuthProvider provider) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.role = UserRole.USER;
        this.deliveryConditionType = DeliveryConditionType.NONE;
        this.conditionFulfilled = true;

        // 첫 번째 provider 등록
        addProvider(provider, null);

        this.timeLetterPushEnabled = true;
        this.mindRecordPushEnabled = true;
        this.afterNotePushEnabled = true;
    }

    public void updateProfile(String name, String phone, String profileImageUrl) {
        if (name != null) {
            if (name.isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            this.name = name;
        }
        if (phone != null && !phone.isBlank()) {
            this.phone = phone;
        }
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void updatePushSettings(Boolean timeLetter, Boolean mindRecord, Boolean afterNote) {
        if (timeLetter != null) { this.timeLetterPushEnabled = timeLetter; }
        if (mindRecord != null) { this.mindRecordPushEnabled = mindRecord; }
        if (afterNote != null) { this.afterNotePushEnabled = afterNote; }
    }

    public void updateRole(UserRole role) {
        if (role != null) {
            this.role = role;
        }
    }

    public void updateDeliveryCondition(DeliveryConditionType conditionType, Integer inactivityPeriodDays, LocalDate specificDate) {
        if (conditionType == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.deliveryConditionType = conditionType;

        switch (conditionType) {
            case NONE -> {
                this.inactivityPeriodDays = null;
                this.specificDate = null;
                this.conditionFulfilled = true;
            }
            case DEATH_CERTIFICATE -> {
                this.inactivityPeriodDays = null;
                this.specificDate = null;
                this.conditionFulfilled = false;
            }
            case INACTIVITY -> {
                if (inactivityPeriodDays == null || inactivityPeriodDays <= 0) {
                    throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                }
                this.inactivityPeriodDays = inactivityPeriodDays;
                this.specificDate = null;
                this.conditionFulfilled = false;
            }
            case SPECIFIC_DATE -> {
                if (specificDate == null) {
                    throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                }
                this.inactivityPeriodDays = null;
                this.specificDate = specificDate;
                this.conditionFulfilled = false;
            }
        }
    }

    public void fulfillCondition() {
        this.conditionFulfilled = true;
    }

    public boolean isDeliveryConditionMet() {
        return switch (this.deliveryConditionType) {
            case NONE -> true;
            case DEATH_CERTIFICATE -> this.conditionFulfilled;
            case INACTIVITY -> this.inactivityPeriodDays != null
                    && this.updatedAt != null
                    && this.updatedAt.isBefore(LocalDateTime.now().minusDays(this.inactivityPeriodDays));
            case SPECIFIC_DATE -> this.specificDate != null && !LocalDate.now().isBefore(this.specificDate);
        };
    }



    /**
     * 새로운 provider를 사용자에게 연동
     *
     * @param provider 추가할 소셜 로그인 제공자
     */
    public void addProvider(AuthProvider provider, String providerId) {
        if (provider == null) {
            return;
        }

        UserProvider existing = this.providers.stream()
                .filter(p -> p.getProvider() == provider)
                .findFirst()
                .orElse(null);

        if (existing == null) {
            this.providers.add(UserProvider.builder()
                    .user(this)
                    .provider(provider)
                    .providerId(providerId)
                    .build());
        } else if (providerId != null && (existing.getProviderId() == null || existing.getProviderId().isBlank())) {
            existing.updateProviderId(providerId);
        }
    }

    /**
     * 사용자가 특정 provider를 통해 로그인할 수 있는지 확인
     *
     * @param provider 확인할 소셜 로그인 제공자
     * @return provider가 연동되어 있으면 true
     */
    public boolean hasProvider(AuthProvider provider) {
        return this.providers.stream().anyMatch(p -> p.getProvider() == provider);
    }

    /**
     * 사용자가 연동한 provider 목록을 반환
     *
     * @return provider Set (빈 Set이 아님을 보장)
     */
    public Set<AuthProvider> getProviders() {
        return this.providers.stream()
                .map(UserProvider::getProvider)
                .collect(Collectors.toSet());
    }
}