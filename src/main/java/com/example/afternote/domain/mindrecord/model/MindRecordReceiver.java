package com.example.afternote.domain.mindrecord.model;

import com.example.afternote.domain.receiver.model.Receiver;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mind_record_receiver", uniqueConstraints = {
                @UniqueConstraint(columnNames = {"mind_record_id", "receiver_id"})})
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

    @Column(nullable = false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public MindRecordReceiver(MindRecord mindRecord, Receiver receiver) {
        this.mindRecord = mindRecord;
        this.receiver = receiver;
        this.enabled = true;
    }

    public static MindRecordReceiver create(MindRecord mindRecord, Receiver receiver) {
        MindRecordReceiver link = new MindRecordReceiver();
        link.mindRecord = mindRecord;
        link.receiver = receiver;
        link.enabled = true;
        return link;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}
