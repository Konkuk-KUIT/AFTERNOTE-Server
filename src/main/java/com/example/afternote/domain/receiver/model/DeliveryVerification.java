package com.example.afternote.domain.receiver.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_verification")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class DeliveryVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private String deathCertificateUrl;

    @Column(nullable = false)
    private String familyRelationCertificateUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Column
    private String adminNote;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public DeliveryVerification(Long userId, Long receiverId, String deathCertificateUrl,
                                 String familyRelationCertificateUrl) {
        this.userId = userId;
        this.receiverId = receiverId;
        this.deathCertificateUrl = deathCertificateUrl;
        this.familyRelationCertificateUrl = familyRelationCertificateUrl;
        this.status = VerificationStatus.PENDING;
    }

    public void approve(String note) {
        this.status = VerificationStatus.APPROVED;
        this.adminNote = note;
    }

    public void reject(String note) {
        this.status = VerificationStatus.REJECTED;
        this.adminNote = note;
    }
}
