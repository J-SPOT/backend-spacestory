package com.juny.spacestory.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RequestCreateReservation(
    @NotNull(message = "userId cannot be null.") UUID userId,
    @NotNull(message = "reservationDate cannot be null.") @Future LocalDate reservationDate,
    @NotNull(message = "startTime cannot be null.") LocalTime startTime,
    @NotNull(message = "endTime cannot be null.") LocalTime endTime,
    @NotNull(message = "isSelf cannot be null.") Boolean isUser) {}
