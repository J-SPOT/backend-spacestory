package com.juny.spacestory.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReqReservation(
  @Schema(description = "예약 날짜", example = "2024-08-09", type = "string")
  LocalDate reservationDate,

  @Schema(description = "시작 시간", example = "10:00:00", type = "string")
  LocalTime startTime,

  @Schema(description = "종료 시간", example = "12:00:00", type = "string")
  LocalTime endTime) {
}
