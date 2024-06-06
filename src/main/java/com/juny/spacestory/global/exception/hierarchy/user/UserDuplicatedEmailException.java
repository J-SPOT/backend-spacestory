package com.juny.spacestory.global.exception.hierarchy.user;

import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.ErrorCode;

public class UserDuplicatedEmailException extends BadRequestException {

  public UserDuplicatedEmailException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserDuplicatedEmailException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
