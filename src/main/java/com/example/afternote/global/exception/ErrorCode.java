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
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, 411, "아이디 또는 비밀번호가 일치하지 않습니다."),

    // 비밀번호 불일치
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, 412, "아이디 또는 비밀번호가 일치하지 않습니다."),

    // 비밀번호 형식 오류
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, 413, "비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다."),

    // 닉네임 중복
    DUPLICATE_NAME(HttpStatus.CONFLICT, 414, "이미 사용 중인 이름입니다."),

    // 계정 비활성화
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, 415, "비활성화된 계정입니다."),

    NEWPASSWORD_MATCH(HttpStatus.BAD_REQUEST, 416, "새 비밀번호와 같습니다."),

    INVALID_EMAIL_VERIFICATION(HttpStatus.BAD_REQUEST, 417, "인증번호가 유효하지 않습니다."),

    // 소셜 로그인 실패
    SOCIAL_LOGIN_FAILED(HttpStatus.BAD_REQUEST, 418, "소셜 로그인에 실패했습니다."),

    // 지원하지 않는 소셜 로그인 제공자
    UNSUPPORTED_SOCIAL_LOGIN(HttpStatus.BAD_REQUEST, 419, "지원하지 않는 소셜 로그인 제공자입니다."),

    // ======================================
    // 4. 타임레터 관련 오류 (code: 420 ~ 429)
    // ======================================
    TIME_LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, 420, "타임레터를 찾을 수 없습니다."),
    TIME_LETTER_ACCESS_DENIED(HttpStatus.FORBIDDEN, 421, "해당 타임레터에 대한 권한이 없습니다."),
    TIME_LETTER_ALREADY_SENT(HttpStatus.BAD_REQUEST, 422, "이미 발송된 타임레터는 수정/삭제할 수 없습니다."),
    TIME_LETTER_INVALID_STATUS(HttpStatus.BAD_REQUEST, 423, "유효하지 않은 상태 변경입니다."),
    TIME_LETTER_REQUIRED_FIELDS(HttpStatus.BAD_REQUEST, 424, "정식 등록 시 제목, 내용, 발송일시는 필수입니다."),
    TIME_LETTER_INVALID_SEND_DATE(HttpStatus.BAD_REQUEST, 425, "발송일시는 현재 시간 이후여야 합니다."),

    // ======================================                                                                                                                                                               
    // 5. 요청 값 검증 오류 (code: 430 ~)                                                                                                                                                                   
    // ======================================                                                                                                                                                               
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 430, "요청 값이 올바르지 않습니다."),                                                                                                                       

    // ======================================                                                                                                                                                               
    // 6. 마음의 기록(MindRecord) 관련 오류 (code: 440 ~)                                                                                                                                                   
    // ======================================                                                                                                                                                               
    MIND_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, 440, "마음의 기록을 찾을 수 없습니다."),                                                                                                                    
    MIND_RECORD_FORBIDDEN(HttpStatus.FORBIDDEN, 441, "해당 마음의 기록에 대한 권한이 없습니다."),                                                                                                           
    DAILY_QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, 442, "데일리 질문을 찾을 수 없습니다."),                                                                                                                 
    DEEP_THOUGHT_CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, 443, "깊은 생각 카테고리는 필수입니다."),                                                                                                        
    MIND_RECORD_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, 444, "마음의 기록 내용은 필수입니다."),                                                                                                            
    DAILY_QUESTION_REQUIRED(HttpStatus.BAD_REQUEST, 445, "데일리 질문 ID는 필수입니다."),
    MIND_RECORD_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, 446, "마음의 기록 제목은 필수입니다."),
    RECEIVER_FORBIDDEN(HttpStatus.FORBIDDEN, 448, "해당 수신인에 대한 접근 권한이 없습니다."),

    // ======================================
    // 7. 애프터노트 관련 오류 (code: 460 ~ 479)                                                                                                                                                            
    // ======================================                                                                                                                                                               
    AFTERNOTE_NOT_FOUND(HttpStatus.NOT_FOUND, 460, "애프터노트를 찾을 수 없습니다."),                                                                                                                       
    AFTERNOTE_ACCESS_DENIED(HttpStatus.FORBIDDEN, 461, "해당 애프터노트에 대한 권한이 없습니다."),                                                                                                          
    CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, 462, "카테고리는 필수입니다."),                                                                                                                               
    SOCIAL_CREDENTIALS_REQUIRED(HttpStatus.BAD_REQUEST, 463, "SOCIAL 카테고리는 계정 정보(credentials)가 필수입니다."),                                                                                     
    SOCIAL_ACCOUNT_ID_REQUIRED(HttpStatus.BAD_REQUEST, 464, "계정 ID는 필수입니다."),                                                                                                                       
    SOCIAL_ACCOUNT_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, 465, "계정 비밀번호는 필수입니다."),                                                                                                           
    GALLERY_RECEIVERS_REQUIRED(HttpStatus.BAD_REQUEST, 466, "GALLERY 카테고리는 수신자(receivers)가 필수입니다."),                                                                                          
    GALLERY_RECEIVER_ID_REQUIRED(HttpStatus.BAD_REQUEST, 467, "수신자 ID는 필수입니다."),                                                                                                                   
    RECEIVER_NOT_FOUND(HttpStatus.NOT_FOUND, 468, "수신자를 찾을 수 없습니다."),
    PLAYLIST_REQUIRED(HttpStatus.BAD_REQUEST, 469, "PLAYLIST 카테고리는 플레이리스트(playlist)가 필수입니다."),                                                                                             
    PLAYLIST_SONGS_REQUIRED(HttpStatus.BAD_REQUEST, 470, "플레이리스트에는 최소 1곡 이상이 필요합니다."),                                                                                                   
    PLAYLIST_SONG_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, 471, "곡 제목은 필수입니다."),                                                                                                                     
    PLAYLIST_SONG_ARTIST_REQUIRED(HttpStatus.BAD_REQUEST, 472, "아티스트는 필수입니다."),                                                                                                                   
    PROCESS_METHOD_REQUIRED(HttpStatus.BAD_REQUEST, 473, "처리 방법(processMethod)은 필수입니다."),                                                                                                         
    ACTIONS_REQUIRED(HttpStatus.BAD_REQUEST, 474, "액션(actions)은 최소 1개 이상 필요합니다."),                                                                                                             
    CATEGORY_CANNOT_BE_CHANGED(HttpStatus.BAD_REQUEST, 475, "카테고리는 변경할 수 없습니다."),                                                                                                              
    INVALID_FIELD_FOR_SOCIAL(HttpStatus.BAD_REQUEST, 476, "SOCIAL 카테고리는 credentials만 수정할 수 있습니다."),                                                                                           
    INVALID_FIELD_FOR_GALLERY(HttpStatus.BAD_REQUEST, 477, "GALLERY 카테고리는 receivers만 수정할 수 있습니다."),                                                                                           
    INVALID_FIELD_FOR_PLAYLIST(HttpStatus.BAD_REQUEST, 478, "PLAYLIST 카테고리는 playlist만 수정할 수 있습니다."),                                                                                          
    FIELD_CANNOT_BE_EMPTY(HttpStatus.BAD_REQUEST, 479, "필드 값은 공백일 수 없습니다."),                                                                                                                    
    ATMOSPHERE_CANNOT_BE_EMPTY(HttpStatus.BAD_REQUEST, 480, "분위기(atmosphere)는 공백일 수 없습니다."),                                                                                                    
    VIDEO_URL_CANNOT_BE_EMPTY(HttpStatus.BAD_REQUEST, 481, "비디오 URL은 공백일 수 없습니다."),                                                                                                             
    THUMBNAIL_URL_CANNOT_BE_EMPTY(HttpStatus.BAD_REQUEST, 482, "썸네일 URL은 공백일 수 없습니다."),                                                                                                         

    // ======================================                                                                                                                                                               
    // 8. 암호화 관련 오류 (code: 490 ~)                                                                                                                                                                    
    // ======================================                                                                                                                                                               
    ENCRYPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 490, "암호화 처리 중 오류가 발생했습니다."),                                                                                                        
    DECRYPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 491, "복호화 처리 중 오류가 발생했습니다."),

    // 소셜 로그인 사용자의 일반 로그인 시도
    SOCIAL_LOGIN_USER(HttpStatus.BAD_REQUEST, 492, "소셜 로그인으로 가입한 계정입니다. 소셜 로그인을 이용해주세요."),

    // ======================================
    // 9. S3/이미지 관련 오류 (code: 493 ~)
    // ======================================
    PRESIGNED_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 493, "Presigned URL 생성에 실패했습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, 494, "허용되지 않는 파일 확장자입니다. (jpg, jpeg, png, gif, webp, heic, mp4, mov, mp3, m4a, wav, pdf 허용)"),
    INVALID_DIRECTORY(HttpStatus.BAD_REQUEST, 495, "허용되지 않는 디렉토리입니다."),

    // ======================================
    // 10. 수신자 인증 관련 오류 (code: 496 ~)
    // ======================================
    INVALID_AUTH_CODE(HttpStatus.NOT_FOUND, 496, "유효하지 않은 인증번호입니다."),

    //gemini api
    GEMINI_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 497, "LLM API 가 실패하였습니다. 원인은 토큰 만료, 시간 초과 등이 있습니다."),

    // ======================================
    // 11. 전달 조건/인증 관련 오류 (code: 600 ~)
    // ======================================
    VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 602, "인증 요청을 찾을 수 없습니다."),
    VERIFICATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, 604, "이미 처리된 인증 요청입니다."),
    ADMIN_REQUIRED(HttpStatus.FORBIDDEN, 605, "관리자 권한이 필요합니다."),
    CONDITION_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, 606, "설정된 전달 조건과 요청이 일치하지 않습니다."),
    INVALID_DELIVERY_CONDITION(HttpStatus.BAD_REQUEST, 607, "전달 조건 요청이 올바르지 않습니다."),
    VERIFICATION_ALREADY_SUBMITTED(HttpStatus.CONFLICT, 608, "이미 대기 중인 인증 요청이 존재합니다."),
    DELIVERY_CONDITION_NOT_MET(HttpStatus.FORBIDDEN, 609, "아직 전달 조건이 충족되지 않았습니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;



}
