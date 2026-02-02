package com.example.afternote.global.util;

import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * ChaCha20-Poly1305 암호화/복호화 유틸리티
 * 모바일 환경에 최적화된 암호화 방식 (ARM 프로세서에서 AES보다 빠름)
 */
@Component
public class AesEncryptionUtil {

    private static final String ALGORITHM = "ChaCha20-Poly1305";
    private static final int NONCE_LENGTH = 12; // ChaCha20-Poly1305는 96-bit nonce 사용
    private static final int KEY_SIZE = 32; // ChaCha20 requires 256-bit (32-byte) key

    static {
        // Bouncy Castle Provider 등록
        Security.addProvider(new BouncyCastleProvider());
    }

    @Value("${security.chacha20.secret-key}")
    private String secretKey;

    @PostConstruct
    private void validateKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != KEY_SIZE) {
           throw new CustomException(ErrorCode.ENCRYPTION_FAILED);
        }
    }

    /**
     * 암호화
     */
    public String encrypt(String plainText) {
        try {
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "ChaCha20");
            
            // 랜덤 nonce 생성 (매번 다른 값)
            byte[] nonce = new byte[NONCE_LENGTH];
            new SecureRandom().nextBytes(nonce);
            
            IvParameterSpec ivSpec = new IvParameterSpec(nonce);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // nonce + encrypted 를 함께 Base64 인코딩
            byte[] combined = new byte[nonce.length + encrypted.length];
            System.arraycopy(nonce, 0, combined, 0, nonce.length);
            System.arraycopy(encrypted, 0, combined, nonce.length, encrypted.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.ENCRYPTION_FAILED);
        }
    }

    /**
     * 복호화
     */
    /**
     * 복호화
     */
    public String decrypt(String encryptedText) {

        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            
            // nonce와 encrypted 분리
            byte[] nonce = new byte[NONCE_LENGTH];
            byte[] encrypted = new byte[combined.length - NONCE_LENGTH];
            System.arraycopy(combined, 0, nonce, 0, NONCE_LENGTH);
            System.arraycopy(combined, NONCE_LENGTH, encrypted, 0, encrypted.length);
            
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "ChaCha20");
            IvParameterSpec ivSpec = new IvParameterSpec(nonce);

            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.DECRYPTION_FAILED);
        }
    }
}
