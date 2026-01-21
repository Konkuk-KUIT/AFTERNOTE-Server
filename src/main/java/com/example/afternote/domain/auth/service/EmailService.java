package com.example.afternote.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate; // 이미 설정해둔 RedisTemplate 사용
    
    @Value("${spring.mail.username}")
    private String senderEmail;

    // 1. 인증번호 전송 로직
    public void sendCode(String toEmail) {
        // 랜덤 인증번호 생성
        String authCode = createCode();

        // 이메일 폼 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[AfterNote] 이메일 인증 번호입니다.");
        message.setText("인증 번호는 [" + authCode + "] 입니다. 3분 안에 입력해주세요.");
        message.setFrom(senderEmail);  // 환경변수에서 가져온 발신자 이메일 사용

        // 이메일 발송
        javaMailSender.send(message);

        // Redis에 저장 (Key: 이메일, Value: 인증코드, 유효시간: 3분)
        // "EMAIL_AUTH:" 접두어를 붙여서 다른 데이터와 구분하면 좋습니다.
        redisTemplate.opsForValue().set("EMAIL:" + toEmail, authCode, 3, TimeUnit.MINUTES);
    }

    // 2. 인증번호 검증 로직
    public boolean verifyCode(String email, String inputCode) {
        String redisCode = redisTemplate.opsForValue().get("EMAIL:" + email);

        // 코드가 존재하고, 입력한 코드와 일치하면 true
        return redisCode != null && redisCode.equals(inputCode);
    }

    // 6자리 랜덤 숫자 생성
    private String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }
        return key.toString();
    }
}