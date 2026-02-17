package com.example.afternote.domain.mindrecord.event;

public class MindRecordCreatedEvent {
    private final Long mindRecordId;

    public MindRecordCreatedEvent(Long mindRecordId) {
        this.mindRecordId = mindRecordId;
    }

    public Long getMindRecordId() {
        return mindRecordId;
    }
}
