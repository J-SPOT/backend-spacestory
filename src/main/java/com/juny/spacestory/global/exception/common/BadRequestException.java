package com.juny.spacestory.global.exception.common;

import com.juny.spacestory.global.exception.ErrorCode;

public class BadRequestException extends CustomBusinessException {

  public BadRequestException(ErrorCode errorCode) {
    super(errorCode);
  }

  public BadRequestException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
