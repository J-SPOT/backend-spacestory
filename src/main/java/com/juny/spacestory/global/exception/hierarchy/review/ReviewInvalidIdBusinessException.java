package com.juny.spacestory.global.exception.hierarchy.review;

import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.common.CustomBusinessException;
import com.juny.spacestory.global.exception.ErrorCode;

public class ReviewInvalidIdBusinessException extends BadRequestException {

  public ReviewInvalidIdBusinessException(ErrorCode errorCode) {
    super(ErrorCode.REVIEW_INVALID_ID);
  }

  public ReviewInvalidIdBusinessException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
