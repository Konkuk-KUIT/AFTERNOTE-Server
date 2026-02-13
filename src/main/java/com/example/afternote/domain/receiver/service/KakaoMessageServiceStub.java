package com.example.afternote.domain.receiver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KakaoMessageServiceStub implements KakaoMessageService {

    @Override
    public void sendAuthCode(String phone, String authCode, String senderName, String receiverName) {
        log.info("[KakaoTalk Stub] 수신자 인증번호 발송 - phone: {}, authCode: {}, sender: {}, receiver: {}",
                phone, authCode, senderName, receiverName);
    }
}
