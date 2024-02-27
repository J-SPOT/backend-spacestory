package com.juny.spacestory.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record RequestUpdateReservation(
        @NotNull(message = "userId cannot be null.")
        Long userId,
        @NotNull(message = "spaceId cannot be null.")
        Long spaceId,
        @NotNull(message = "reservationDate cannot be null.")
        @Future
        LocalDate reservationDate,
        @NotNull(message = "startTime cannot be null.")
        LocalTime startTime,
        @NotNull(message = "endTime cannot be null.")
        LocalTime endTime,
        @NotNull(message = "isUser cannot be null.")
        Boolean isUser,
        @NotNull(message = "isReserved cannot be null.")
        Boolean isReserved) {
}
