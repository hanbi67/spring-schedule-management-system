package com.example.schedulemanagementsystem.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final List<FieldError> fieldErrors;

    @Getter
    @AllArgsConstructor
    public static class FieldError {
        //어떤 필드가 문제인지(예: "email")
        private final String field;
        //사용자가 넣은 값(예: "")
        private final String rejectedValue;
        //출력될 에러 메세지(예: "이메일은 필수입니다.")
        private final String reason;

        public FieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue == null ? null : String.valueOf(rejectedValue);
            this.reason = reason;
        }
    }



}
