package com.example.security.codes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //METHOD
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "유효성 검증 오류"),
    METHOD_NOT_ALLOW(HttpStatus.METHOD_NOT_ALLOWED.value(), "허락되지 않은 POST 요청"),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "요청 URL 오류"),
    USER_ALEADY_EXISTS(HttpStatus.CONFLICT.value(), "가입된 사용자 입니다."),

    //JWT
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED.value(), "토큰이 만료되었습니다."),
    MALFORMED_JWT(HttpStatus.UNAUTHORIZED.value(), "올바르지 않은 토큰입니다."),
    SIGNATURE_JWT(HttpStatus.UNAUTHORIZED.value(), "토큰이 유효하지 않습니다."),
    CATEGORY_NOT_REFRESH(HttpStatus.UNAUTHORIZED.value(), "Refresh 토큰이 아닙니다."),
    TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED.value(), "Refresh 토큰이 존재하지 않습니다."),
    //AUTHORIZATION
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "사용자 인증 실패");



    private int status;
    private String message;
}
