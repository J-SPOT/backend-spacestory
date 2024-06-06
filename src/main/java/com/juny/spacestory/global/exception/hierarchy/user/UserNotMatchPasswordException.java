package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.ErrorCode;

public class UserNotMatchPasswordException extends BadRequestException {

  public UserNotMatchPasswordException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserNotMatchPasswordException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
