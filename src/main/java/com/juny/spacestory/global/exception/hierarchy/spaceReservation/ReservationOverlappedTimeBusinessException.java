package com.juny.spacestory.global.exception.hierarchy.spaceReservation;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.common.CustomBusinessException;

public class ReservationOverlappedTimeBusinessException extends BadRequestException {
  public ReservationOverlappedTimeBusinessException(ErrorCode errorCode) {
    super(ErrorCode.RESERVATION_OVERLAPPED_TIME);
  }
}
