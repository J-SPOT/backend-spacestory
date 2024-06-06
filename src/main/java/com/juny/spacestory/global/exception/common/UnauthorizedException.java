package com.juny.spacestory.global.exception.common;

import com.juny.spacestory.global.exception.ErrorCode;

public class UnauthorizedException extends CustomBusinessException {

  public UnauthorizedException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UnauthorizedException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
