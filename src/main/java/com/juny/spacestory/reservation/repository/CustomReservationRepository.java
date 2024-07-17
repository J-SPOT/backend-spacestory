package com.juny.spacestory.reservation.repository;

import com.juny.spacestory.reservation.entity.Reservation;
import java.time.LocalDate;
import java.util.List;

public interface CustomReservationRepository {

  List<Reservation> findActiveReservations(
    Long spaceId,
    LocalDate reservationDate
  );
}
