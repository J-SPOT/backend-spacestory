package com.juny.spacestory.exception.host;

import com.juny.spacestory.exception.InvalidFormatException;
import com.juny.spacestory.exception.global.ErrorCode;

public class HostInvalidIdException extends InvalidFormatException {
    public HostInvalidIdException(ErrorCode errorCode) {
        super(ErrorCode.HOST_INVALID_ID);
    }
}
