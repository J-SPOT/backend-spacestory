package com.juny.spacestory.global.exception.hierarchy.space;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class SpaceInvalidDetailedTypeBusinessException extends BadRequestException {

  public SpaceInvalidDetailedTypeBusinessException(ErrorCode errorCode) {
    super(ErrorCode.SPACE_INVALID_DETAILED_TYPE);
  }

  public SpaceInvalidDetailedTypeBusinessException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
