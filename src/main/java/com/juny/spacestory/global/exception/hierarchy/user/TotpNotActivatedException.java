package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class TotpNotActivatedException extends CustomBusinessException {

  public TotpNotActivatedException(ErrorCode errorCode) {
    super(errorCode);
  }

  public TotpNotActivatedException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
