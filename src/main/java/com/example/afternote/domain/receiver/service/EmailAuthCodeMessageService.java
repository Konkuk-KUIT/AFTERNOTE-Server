package com.example.afternote.domain.receiver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth-code.email.enabled", havingValue = "true")
public class EmailAuthCodeMessageService implements AuthCodeMessageService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendAuthCode(String email, String authCode, String senderName, String receiverName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(senderEmail);
        message.setSubject("[AfterNote] 수신자 인증번호 안내");
        message.setText(buildBody(receiverName, senderName, authCode));
        javaMailSender.send(message);
    }

    private String buildBody(String receiverName, String senderName, String authCode) {
        return "안녕하세요, " + receiverName + "님.\n"
                + "\n"
                + senderName + "님이 AfterNote를 통해 당신에게 메시지를 남겼습니다.\n"
                + "\n"
                + "아래 인증번호를 입력하면 전달된 메시지를 확인할 수 있습니다.\n"
                + "\n"
                + "인증번호: " + authCode + "\n"
                + "\n"
                + "감사합니다.\n"
                + "AfterNote 팀";
    }
}
