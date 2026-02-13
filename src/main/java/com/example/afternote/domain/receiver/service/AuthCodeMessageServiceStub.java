package com.example.afternote.domain.receiver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.auth-code.email.enabled", havingValue = "false", matchIfMissing = true)
public class AuthCodeMessageServiceStub implements AuthCodeMessageService {

    @Override
    public void sendAuthCode(String email, String authCode, String senderName, String receiverName) {
        log.info("[Email Stub] 수신자 인증번호 발송 - email: {}, authCode: {}, sender: {}, receiver: {}",
                email, authCode, senderName, receiverName);
    }
}
