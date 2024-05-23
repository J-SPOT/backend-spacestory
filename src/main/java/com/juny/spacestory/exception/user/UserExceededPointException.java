package com.juny.spacestory.exception.user;

import com.juny.spacestory.exception.InvalidFormatException;
import com.juny.spacestory.exception.global.ErrorCode;

public class UserExceededPointException extends InvalidFormatException {
    public UserExceededPointException(ErrorCode errorCode) {
        super(ErrorCode.USER_NOT_ENOUGH_POINT);
    }
}
