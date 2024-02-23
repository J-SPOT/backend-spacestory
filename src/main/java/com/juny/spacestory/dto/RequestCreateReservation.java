package com.juny.spacestory.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record RequestCreateReservation(Long userId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime) {
}
