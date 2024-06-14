package com.example.security.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int status;
    private String message;
    private List<?> errors;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class FieldError {
        private String field;
        private String value;
        private String reason;

        public FieldError of(org.springframework.validation.FieldError fieldError) {
            this.field = fieldError.getField();
            this.value = (String) fieldError.getRejectedValue();
            this.reason = fieldError.getDefaultMessage();

            return this;
        }
    }

    public ErrorResponse (ErrorCode errorCode, BindingResult e) {

        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.errors=e.getFieldErrors().stream()
                .map(f->new FieldError().of(f))
                .collect(Collectors.toList());

    }

    public ErrorResponse(ErrorCode errorCode, String error) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.errors = new ArrayList<>(List.of(error));
    }

    public ErrorResponse(String message) {
        this.message = message;
    }
}

