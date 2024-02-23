package com.juny.spacestory.repository;

import com.juny.spacestory.domain.SpaceReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<SpaceReservation, Long> {
    List<SpaceReservation> findBySpaceIdAndReservationDateAndIsReservedTrue(Long spaceId, LocalDate reservationDate);

    List<SpaceReservation> findByUserId(Long userId);
}
