package com.juny.spacestory.exception.spaceReservation;

import com.juny.spacestory.exception.global.ErrorCode;
import com.juny.spacestory.exception.InvalidFormatException;

public class ReservationMinimumTimeException extends InvalidFormatException {
    public ReservationMinimumTimeException(ErrorCode errorCode) {
        super(ErrorCode.RESERVATION_MINIMUM_TIME);
    }
}
