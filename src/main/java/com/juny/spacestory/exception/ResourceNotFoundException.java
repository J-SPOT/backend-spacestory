package com.juny.spacestory.exception;

import com.juny.spacestory.exception.global.ErrorCode;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
