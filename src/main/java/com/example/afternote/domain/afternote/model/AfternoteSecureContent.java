package com.example.afternote.domain.afternote.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "afternote_secure_content")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AfternoteSecureContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "afternote_id", nullable = false)
    private Afternote afternote;
    
    @Column(name = "key_name", nullable = false, length = 50)
    private String keyName;
    
    @Column(name = "encrypted_value", columnDefinition = "TEXT")
    private String encryptedValue;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
