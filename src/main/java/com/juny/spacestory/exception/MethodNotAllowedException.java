package com.juny.spacestory.exception;

import com.juny.spacestory.exception.global.ErrorCode;

public class MethodNotAllowedException extends CustomException {
    public MethodNotAllowedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
