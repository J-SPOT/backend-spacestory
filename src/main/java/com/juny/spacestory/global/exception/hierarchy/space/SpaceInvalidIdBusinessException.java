package com.juny.spacestory.global.exception.hierarchy.space;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class SpaceInvalidIdBusinessException extends BadRequestException {

  public SpaceInvalidIdBusinessException(ErrorCode errorCode) {
    super(ErrorCode.SPACE_INVALID_ID);
  }

  public SpaceInvalidIdBusinessException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
