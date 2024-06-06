package com.juny.spacestory.global.exception.common;

import com.juny.spacestory.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CustomBusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public CustomBusinessException(ErrorCode errorCode) {
    super(errorCode.getMsg());
    this.errorCode = errorCode;
  }

  public CustomBusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
