package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class UserUnAuthorizedModifyBusinessException extends CustomBusinessException {

  public UserUnAuthorizedModifyBusinessException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserUnAuthorizedModifyBusinessException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
