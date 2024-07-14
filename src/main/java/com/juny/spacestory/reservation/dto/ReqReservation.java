package com.juny.spacestory.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReqReservation(
    LocalDate reservationDate,
    LocalTime startTime,
    LocalTime endTime) {

}
