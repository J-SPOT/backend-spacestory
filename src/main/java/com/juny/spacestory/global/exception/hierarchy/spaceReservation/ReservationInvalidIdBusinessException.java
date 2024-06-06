package com.juny.spacestory.global.exception.hierarchy.spaceReservation;

import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.common.CustomBusinessException;
import com.juny.spacestory.global.exception.ErrorCode;

public class ReservationInvalidIdBusinessException extends BadRequestException {

  public ReservationInvalidIdBusinessException(ErrorCode errorCode) {
    super(ErrorCode.RESERVATION_INVALID_ID);
  }

  public ReservationInvalidIdBusinessException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
