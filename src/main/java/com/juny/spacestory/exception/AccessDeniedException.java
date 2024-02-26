package com.juny.spacestory.exception;

import com.juny.spacestory.exception.global.ErrorCode;

public class AccessDeniedException extends CustomException {
    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}