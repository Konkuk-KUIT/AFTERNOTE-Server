package com.example.afternote.domain.receiver.model;

import com.example.afternote.domain.mindrecord.model.MindRecord;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mind_record_receiver")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MindRecordReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mind_record_id", nullable = false)
    private MindRecord mindRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Receiver receiver;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public MindRecordReceiver(MindRecord mindRecord, Receiver receiver) {
        this.mindRecord = mindRecord;
        this.receiver = receiver;
    }
}
