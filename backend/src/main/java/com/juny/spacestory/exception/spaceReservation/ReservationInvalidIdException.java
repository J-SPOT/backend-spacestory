package com.juny.spacestory.exception.spaceReservation;

import com.juny.spacestory.exception.global.ErrorCode;
import com.juny.spacestory.exception.InvalidFormatException;

public class ReservationInvalidIdException extends InvalidFormatException {
    public ReservationInvalidIdException(ErrorCode errorCode) {
        super(ErrorCode.RESERVATION_INVALID_ID);
    }
}
