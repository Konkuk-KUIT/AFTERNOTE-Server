package com.example.afternote.domain.receiver.service;

public interface AuthCodeMessageService {
    void sendAuthCode(String email, String authCode, String senderName, String receiverName);
}
