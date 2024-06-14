package com.example.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "유효성 검증 오류"),


    METHOD_POST_NOT_ALLOW(HttpStatus.METHOD_NOT_ALLOWED.value(), "허락되지 않은 POST 요청"),
    FILE_INPUT_VALUE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 입출력 오류"),


    // Exception;
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "요청 URL 오류"),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "사용자 인증 실패");




    private int status;
    private String message;
}
