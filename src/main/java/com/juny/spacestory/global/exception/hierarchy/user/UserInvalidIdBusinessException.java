package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.common.CustomBusinessException;
import com.juny.spacestory.global.exception.ErrorCode;

public class UserInvalidIdBusinessException extends CustomBusinessException {

  public UserInvalidIdBusinessException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserInvalidIdBusinessException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
