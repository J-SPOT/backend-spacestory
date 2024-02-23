package com.juny.spacestory.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record RequestUpdateReservation(LocalDate reservationDate, LocalTime startTime, LocalTime endTime, Boolean isReserved) {
}
