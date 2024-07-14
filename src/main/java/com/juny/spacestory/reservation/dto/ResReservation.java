package com.juny.spacestory.reservation.dto;

import com.juny.spacestory.space.dto.ResSpace;
import java.time.LocalDate;
import java.time.LocalTime;

public record ResReservation(
  Long id,
  LocalDate reservationDate,
  LocalTime startTime,
  LocalTime endTime,
  Long fee,
  ResSpace space
) {

}
