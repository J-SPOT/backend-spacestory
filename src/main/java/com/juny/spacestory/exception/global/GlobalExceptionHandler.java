package com.juny.spacestory.exception.global;

import com.juny.spacestory.exception.CustomException;
import com.juny.spacestory.exception.space.SpaceInvalidIdException;
import com.juny.spacestory.exception.spaceReservation.ReservationInvalidIdException;
import com.juny.spacestory.exception.spaceReservation.ReservationMinimumTimeException;
import com.juny.spacestory.exception.spaceReservation.ReservationOverlappedTimeException;
import com.juny.spacestory.exception.user.UserInvalidIdException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            ReservationInvalidIdException.class,
            ReservationMinimumTimeException.class,
            ReservationOverlappedTimeException.class,
            SpaceInvalidIdException.class,
            UserInvalidIdException.class,
    })
    public ResponseEntity<ErrorResponse> handle(CustomException e) {
        return new ResponseEntity<>(ErrorResponse.of(e.getErrorCode()), e.getErrorCode().getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        return new ResponseEntity<>(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR), ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
    }
}
