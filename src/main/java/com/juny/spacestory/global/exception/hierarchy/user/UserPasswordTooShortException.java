package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.ErrorCode;

public class UserPasswordTooShortException extends BadRequestException {

  public UserPasswordTooShortException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserPasswordTooShortException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
