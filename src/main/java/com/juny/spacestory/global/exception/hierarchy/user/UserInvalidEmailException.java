package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class UserInvalidEmailException extends CustomBusinessException {

  public UserInvalidEmailException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserInvalidEmailException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
