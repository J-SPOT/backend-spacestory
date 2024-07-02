package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class UserInvalidPhoneNumberException extends CustomBusinessException {

  public UserInvalidPhoneNumberException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserInvalidPhoneNumberException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
