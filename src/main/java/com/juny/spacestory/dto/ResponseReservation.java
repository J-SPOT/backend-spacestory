package com.juny.spacestory.dto;

import com.juny.spacestory.domain.DetailedType;
import com.juny.spacestory.domain.SpaceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record ResponseReservation(
        Long reservationId,
        LocalDate reservationDate,
        LocalTime startTime,
        LocalTime endTime,
        Long fee,
        SpaceType spaceType,
        String spaceName,
        LocalTime openingTime,
        LocalTime closingTime,
        Integer hourlyRate,
        Integer spaceSize,
        Integer maxCapacity,
        Set<DetailedType> detailedTypes,
        Integer floor,
        boolean hasParking,
        boolean hasElevator
        ) {
}
