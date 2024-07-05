package com.juny.spacestory.reservation.repository;

import com.juny.spacestory.reservation.entity.Reservation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  List<Reservation> findBySpaceIdAndReservationDateAndIsDeletedFalse(
      Long spaceId, LocalDate reservationDate);

  List<Reservation> findByUserId(UUID userId);

  @Query(
      "SELECT sr FROM Reservation sr "
          + "JOIN FETCH sr.space s "
          + "JOIN FETCH s.realEstate re "
          + "JOIN FETCH re.host")
  List<Reservation> findAllReservations();

  List<Reservation> findBySpaceIdAndReservationDate(Long spaceId, LocalDate reservationDate);

  @Query(
      "SELECT r FROM Reservation r JOIN FETCH r.space WHERE r.space.id = :spaceId AND r.reservationDate = :reservationDate")
  List<Reservation> findReservationsBySpaceIdAndDateJPQL(
      Long spaceId, LocalDate reservationDate);
}
