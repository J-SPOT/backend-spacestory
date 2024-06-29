package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.UnauthorizedException;

public class EmailCodeInvalidException extends UnauthorizedException {

  public EmailCodeInvalidException(ErrorCode errorCode) {
    super(errorCode);
  }

  public EmailCodeInvalidException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
