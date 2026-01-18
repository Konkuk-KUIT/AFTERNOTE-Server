package com.example.afternote.global.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드의 파라미터에 사용하여 인증된 사용자의 ID를 주입받습니다.
 * 
 * 사용 예시:
 * @GetMapping("/profile")
 * public Response getProfile(@AuthUser Long userId) {
 *     // userId를 바로 사용
 * }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserId {
}
