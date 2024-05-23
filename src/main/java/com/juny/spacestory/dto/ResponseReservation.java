package com.juny.spacestory.dto;

import com.juny.spacestory.domain.DetailedType;
import com.juny.spacestory.domain.SpaceType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record ResponseReservation(
        @NotNull(message = "reservationId cannot be null.")
        Long id,
        @NotNull(message = "reservationDate cannot be null.")
        @Future
        LocalDate reservationDate,
        @NotNull(message = "startTime cannot be null.")
        LocalTime startTime,
        @NotNull(message = "endTime cannot be null.")
        LocalTime endTime,
        @NotNull(message = "fee cannot be null.")
        Long fee,
        @NotNull(message = "spaceType cannot be null.")
        SpaceType spaceType,
        @NotNull(message = "spaceName cannot be null.")
        String spaceName,
        @NotNull(message = "openingTime cannot be null.")
        LocalTime openingTime,
        @NotNull(message = "closingTime cannot be null.")
        LocalTime closingTime,
        @NotNull(message = "hourlyRate cannot be null.")
        Integer hourlyRate,
        @NotNull(message = "spaceSize cannot be null.")
        Integer spaceSize,
        @NotNull(message = "maxCapacity cannot be null.")
        Integer maxCapacity,
        @NotNull(message = "detailedTypes cannot be null.")
        Set<DetailedType> detailedTypes,
        @NotNull(message = "floor cannot be null.")
        Integer floor,
        @NotNull(message = "hasParking cannot be null.")
        boolean hasParking,
        @NotNull(message = "hasElevator cannot be null.")
        boolean hasElevator) {
}
