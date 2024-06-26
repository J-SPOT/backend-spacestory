package com.juny.spacestory.global.exception.hierarchy.email;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;

public class EmailCodeExpiredException extends BadRequestException {

  public EmailCodeExpiredException(ErrorCode errorCode) {
    super(errorCode);
  }

  public EmailCodeExpiredException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
