package com.juny.spacestory.reservation.repository;

import com.juny.spacestory.reservation.entity.Reservation;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, CustomReservationRepository {

  Page<Reservation> findBySpaceIdAndReservationDate(
    Long spaceId, LocalDate reservationDate, Pageable pageable);

  Page<Reservation> findByUserIdAndDeletedAtIsNull(UUID userId, Pageable pageable);
}
