package com.juny.spacestory.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record TimeSlot(
    @NotNull(message = "startTime cannot be null.") @Future LocalTime startTime,
    @Future @NotNull(message = "endTime cannot be null.") LocalTime endTime) {}
