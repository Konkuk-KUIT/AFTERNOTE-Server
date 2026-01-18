package com.example.afternote.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ======================================
    // 1. 공통 / 인증 / 인가 (code: 999)
    // ======================================
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 999, "인증되지 않은 요청입니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, 999, "인증이 필요합니다. 로그인해주세요."),
    NOT_ENOUGH_PERMISSION(HttpStatus.FORBIDDEN, 999, "권한이 부족합니다."),
    ENDPOINT_NOT_FOUND(HttpStatus.NOT_FOUND, 999, "존재하지 않는 엔드포인트입니다."),

    // ======================================
    // 2. 토큰 관련 오류 (code: 400 ~ 409)
    // ======================================
    // Authorization header 미설정
    MISSING_AUTH_HEADER(HttpStatus.BAD_REQUEST, 400, "Authorization 헤더 미설정"),

    // 쿠키 미설정
    MISSING_COOKIE(HttpStatus.BAD_REQUEST, 401, "쿠키 값 미설정"),

    // 엑세스 토큰 만료
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, 402, "엑세스 토큰 만료"),

    // 유효하지 않은 엑세스 토큰
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, 403, "유효하지 않은 엑세스 토큰"),

    // 엑세스 토큰 타입 미일치
    ACCESS_TOKEN_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, 404, "엑세스 토큰 타입 미일치"),

    // 리프레시 토큰 미설정 (쿠키)
    MISSING_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, 405, "리프레시 토큰 미설정"),

    // 리프레시 토큰 만료
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, 406, "리프레시 토큰 만료"),

    // 유효하지 않은 리프레시 토큰
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, 407, "유효하지 않은 리프레시 토큰"),

    // 리프레시 토큰 타입 미일치
    REFRESH_TOKEN_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, 408, "리프레시 토큰 타입 미일치"),

    // 사용이 제한된 리프레시 토큰
    RESTRICTED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, 409, "사용이 제한된 리프레시 토큰"),

    // ======================================
    // 3. 회원가입/사용자 관련 오류 (code: 410 ~ 419)
    // ======================================
    // 이메일 중복
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, 410, "이미 가입된 이메일입니다."),

    // 사용자를 찾을 수 없음
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 411, "사용자를 찾을 수 없습니다."),

    // 비밀번호 불일치
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, 412, "비밀번호가 일치하지 않습니다."),

    // 비밀번호 형식 오류
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, 413, "비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다."),

    // 닉네임 중복
    DUPLICATE_NAME(HttpStatus.CONFLICT, 414, "이미 사용 중인 이름입니다."),

    // 계정 비활성화
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, 415, "비활성화된 계정입니다."),

    NEWPASSWORD_MATCH(HttpStatus.BAD_REQUEST, 416, "새 비밀번호와 같습니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;



}
