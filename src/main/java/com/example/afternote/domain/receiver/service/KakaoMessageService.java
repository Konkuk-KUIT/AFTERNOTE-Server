package com.example.afternote.domain.receiver.service;

public interface KakaoMessageService {
    void sendAuthCode(String phone, String authCode, String senderName, String receiverName);
}
