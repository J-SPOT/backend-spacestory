package com.juny.spacestory.global.exception.hierarchy.token;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class AccessTokenExpiredException extends CustomBusinessException {

  public AccessTokenExpiredException(ErrorCode errorCode) {
    super(errorCode);
  }

  public AccessTokenExpiredException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
