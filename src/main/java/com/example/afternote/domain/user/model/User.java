package com.example.afternote.domain.user.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    @Column(nullable = false) // 카카오 로그인만 쓸 경우 nullable = true로 변경 고려
    private String password;

    @Setter
    @Column(nullable = false, length = 50)
    private String name;

    @Setter
    @Column
    private String phone; // 연락처 (선택)

    @Setter
    @Column
    private String profileImageUrl; // 프로필 이미지 URL (선택)

    @Enumerated(EnumType.STRING) // DB에 숫자가 아닌 텍스트(ACTIVE)로 저장
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING) // DB에 숫자가 아닌 텍스트(KAKAO, LOCAL)로 저장
    @Column(nullable = false)
    private AuthProvider provider; // ERD 하단에 추가된 provider 필드 반영

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(String email, String password, String name, String phone,
                String profileImageUrl, UserStatus status, AuthProvider provider) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.provider = provider;
    }
}