package com.juny.spacestory.exception.spaceReservation;

import com.juny.spacestory.exception.global.ErrorCode;
import com.juny.spacestory.exception.InvalidFormatException;

public class ReservationOverlappedTimeException extends InvalidFormatException {
    public ReservationOverlappedTimeException(ErrorCode errorCode) {
        super(ErrorCode.RESERVATION_OVERLAPPED_TIME);
    }
}
