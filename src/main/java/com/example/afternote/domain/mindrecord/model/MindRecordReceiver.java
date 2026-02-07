package com.example.afternote.domain.mindrecord.model;

import com.example.afternote.domain.receiver.model.Receiver;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mind_record_receiver", uniqueConstraints = {
                @UniqueConstraint(columnNames = {"mind_record_id", "receiver_id"})})
@Getter
@NoArgsConstructor
public class MindRecordReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연결된 마음의 기록
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mind_record_id", nullable = false)
    private MindRecord mindRecord;

    // 전달 대상 수신인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Receiver receiver;

    // 수신 여부 토글 (on/off)
    @Column(nullable = false)
    private boolean enabled;

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