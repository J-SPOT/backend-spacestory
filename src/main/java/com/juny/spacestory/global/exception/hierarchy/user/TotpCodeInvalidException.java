package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class TotpCodeInvalidException extends CustomBusinessException {

  public TotpCodeInvalidException(ErrorCode errorCode) {
    super(errorCode);
  }

  public TotpCodeInvalidException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
