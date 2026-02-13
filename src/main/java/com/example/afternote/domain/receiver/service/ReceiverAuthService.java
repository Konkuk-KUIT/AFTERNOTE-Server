package com.example.afternote.domain.receiver.service;

import com.example.afternote.domain.receiver.dto.ReceivedAfternoteListResponse;
import com.example.afternote.domain.receiver.dto.ReceivedMindRecordListResponse;
import com.example.afternote.domain.receiver.dto.ReceivedTimeLetterListResponse;
import com.example.afternote.domain.receiver.dto.ReceiverAuthVerifyResponse;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.repository.ReceiverRepository;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceiverAuthService {

    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    private final ReceiverRepository receiverRepository;
    private final UserRepository userRepository;
    private final ReceivedService receivedService;

    public ReceiverAuthVerifyResponse verifyAuthCode(String authCode) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        User sender = userRepository.findById(receiver.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_CODE));
        return ReceiverAuthVerifyResponse.from(receiver, sender.getName());
    }

    public ReceivedTimeLetterListResponse getTimeLettersByAuthCode(String authCode) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        return receivedService.getTimeLetters(receiver.getId());
    }

    public ReceivedAfternoteListResponse getAfternotesByAuthCode(String authCode) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        return receivedService.getAfternotes(receiver.getId());
    }

    public ReceivedMindRecordListResponse getMindRecordsByAuthCode(String authCode) {
        Receiver receiver = findReceiverByAuthCode(authCode);
        return receivedService.getMindRecords(receiver.getId());
    }

    private Receiver findReceiverByAuthCode(String authCode) {
        if (authCode == null || !UUID_PATTERN.matcher(authCode.toLowerCase()).matches()) {
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        }
        return receiverRepository.findByAuthCode(authCode.toLowerCase())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_CODE));
    }
}
