package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.common.CustomBusinessException;
import com.juny.spacestory.global.exception.ErrorCode;

public class UserExceededPointBusinessException extends CustomBusinessException {

  public UserExceededPointBusinessException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserExceededPointBusinessException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
