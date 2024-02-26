package com.juny.spacestory.exception;

import com.juny.spacestory.exception.global.ErrorCode;

public class InvalidFormatException extends CustomException {
    public InvalidFormatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
