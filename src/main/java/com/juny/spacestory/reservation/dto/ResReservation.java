package com.juny.spacestory.reservation.dto;

import com.juny.spacestory.reservation.entity.ReservationStatus;
import com.juny.spacestory.space.dto.ResSpace;
import com.juny.spacestory.user.dto.ResLookUpUser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ResReservation(
  Long id,
  LocalDate reservationDate,
  LocalTime startTime,
  LocalTime endTime,
  Long fee,
  ReservationStatus status,
  LocalDateTime createdAt,
  LocalDateTime deletedAt,
  ResSpace space,
  ResLookUpUser user
) {

}
