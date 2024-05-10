package com.juny.spacestory.exception.user;

import com.juny.spacestory.exception.global.ErrorCode;
import com.juny.spacestory.exception.InvalidFormatException;

public class UserInvalidIdException extends InvalidFormatException {
    public UserInvalidIdException(ErrorCode errorCode) {
        super(ErrorCode.USER_INVALID_ID);
    }
}
