package com.juny.spacestory.exception.user;

import com.juny.spacestory.exception.AccessDeniedException;
import com.juny.spacestory.exception.global.ErrorCode;

public class UserUnAuthorizedModifyException extends AccessDeniedException {
    public UserUnAuthorizedModifyException(ErrorCode errorCode) {
        super(ErrorCode.USER_UNAUTHORIZED_TO_MODIFY);
    }
}
