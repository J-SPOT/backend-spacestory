package com.juny.spacestory.global.exception.hierarchy.host;

import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.common.CustomBusinessException;
import com.juny.spacestory.global.exception.ErrorCode;

public class HostInvalidIdBusinessException extends BadRequestException {

  public HostInvalidIdBusinessException(ErrorCode errorCode) {
    super(ErrorCode.HOST_INVALID_ID);
  }

  public HostInvalidIdBusinessException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
