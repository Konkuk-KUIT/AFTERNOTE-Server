package com.example.afternote.domain.receiver.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "receiver")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용 시 기본 생성자 필수 (보안상 PROTECTED 권장)
@EntityListeners(AuditingEntityListener.class) // 생성일자 자동 주입을 위해 필요
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String relation;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Receiver(String name, String relation, String phone, String email) {
        this.name = name;
        this.relation = relation;
        this.phone = phone;
        this.email = email;
    }
}