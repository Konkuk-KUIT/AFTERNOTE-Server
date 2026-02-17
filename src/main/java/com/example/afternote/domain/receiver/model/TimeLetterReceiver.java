package com.example.afternote.domain.receiver.model;

import com.example.afternote.domain.receiver.model.Receiver; // 패키지명 확인!
import com.example.afternote.domain.timeletter.model.TimeLetter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_letter_receiver")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class TimeLetterReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 편지인지 (TimeLetter FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_letter_id", nullable = false)
    private TimeLetter timeLetter;

    // 누구에게 보내는지 (Receiver FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Receiver receiver;

    @Column(name = "delivered_at", nullable = false)
    private LocalDateTime deliveredAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public TimeLetterReceiver(TimeLetter timeLetter, Receiver receiver, LocalDateTime deliveredAt) {
        this.timeLetter = timeLetter;
        this.receiver = receiver;
        this.deliveredAt = deliveredAt;
    }

    /**
     * 읽음 처리 (멱등성 보장: readAt이 null일 때만 설정)
     */
    public void markAsRead() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
}