package com.example.afternote.domain.mindrecord.emotion.model;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "emotions")
@Getter
@Setter
@NoArgsConstructor
public class Emotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mind_record_id", nullable = false)
    private MindRecord mindRecord;
    
    @Column(length = 30, nullable = false)
    private String keyword;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
