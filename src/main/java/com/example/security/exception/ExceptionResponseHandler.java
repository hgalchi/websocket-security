package com.example.security.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.SignatureException;

//선언적으로 발생하지않는 예외포함
@RestControllerAdvice
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    /**
     * validation 유효성 검증 exception
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.error("handlerInvalideExcecption", ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE, ex.getBindingResult());
        return ResponseEntity.status(status)
                .body(response);
    }

    //JWT exception
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity handleSignatureException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("토큰이 유효하지 않습니다.");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity handleMalformedJwtException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("올바르지 않은 토큰입니다.");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity handleExpiredJwtException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("토큰이 만료되었습니다.");
    }




}
