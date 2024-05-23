package com.juny.spacestory.repository;

import com.juny.spacestory.domain.SpaceReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<SpaceReservation, Long> {
    List<SpaceReservation> findBySpaceIdAndReservationDateAndIsDeletedFalse(Long spaceId, LocalDate reservationDate);

    List<SpaceReservation> findByUserId(Long userId);

    @Query("SELECT sr FROM SpaceReservation sr " +
            "JOIN FETCH sr.space s " +
            "JOIN FETCH s.realEstate re " +
            "JOIN FETCH re.host")
    List<SpaceReservation> findAllReservations();

    List<SpaceReservation> findBySpaceIdAndReservationDate(Long spaceId, LocalDate reservationDate);

    @Query("SELECT r FROM SpaceReservation r JOIN FETCH r.space WHERE r.space.id = :spaceId AND r.reservationDate = :reservationDate")
    List<SpaceReservation> findReservationsBySpaceIdAndDateJPQL(Long spaceId, LocalDate reservationDate);
}
