package com.juny.spacestory.global.exception.hierarchy.token;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class AccessTokenInvalidException extends CustomBusinessException {

  public AccessTokenInvalidException(ErrorCode errorCode) {
    super(errorCode);
  }

  public AccessTokenInvalidException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
