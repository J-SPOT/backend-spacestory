package com.juny.spacestory.global.exception.hierarchy.token;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;

public class RefreshTokenInvalidException extends BadRequestException {

  public RefreshTokenInvalidException(ErrorCode errorCode) {
    super(errorCode);
  }

  public RefreshTokenInvalidException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
