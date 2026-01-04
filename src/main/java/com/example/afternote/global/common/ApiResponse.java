package com.example.afternote.global.common;

import com.example.afternote.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    // 기존 boolean success 삭제 -> status, code 추가
    private int status;
    private int code;
    private String message;
    private T data;

    // 성공 시 (200 OK)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), 200, "성공", data);
    }

    // ★ 핵심: ErrorCode를 받아서 자동으로 채워주는 메서드
    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(
                errorCode.getHttpStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }

    // 예외적인 에러 메시지를 직접 넣어야 할 때
    public static ApiResponse<Void> error(int status, int code, String message) {
        return new ApiResponse<>(status, code, message, null);
    }
}