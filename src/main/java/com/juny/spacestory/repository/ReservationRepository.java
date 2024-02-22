package com.juny.spacestory.repository;

import com.juny.spacestory.domain.SpaceReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<SpaceReservation, Long> {
    List<SpaceReservation> findBySpaceIdAndEndTimeAfter(Long spaceId, LocalDateTime now);
}
