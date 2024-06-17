package com.example.security.exception;

import com.example.security.codes.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorcode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorcode = errorCode;
    }

}
