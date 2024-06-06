package com.juny.spacestory.global.exception.hierarchy.parameter;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class ParameterIsNullOrEmpty extends BadRequestException {

  public ParameterIsNullOrEmpty(ErrorCode errorCode) {
    super(errorCode);
  }

  public ParameterIsNullOrEmpty(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
