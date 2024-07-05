//package com.juny.spacestory.space.dto;
//
//import com.juny.spacestory.space.domain.SpaceType;
//import jakarta.validation.constraints.NotNull;
//
//import java.time.LocalTime;
//
//public record RequestUpdateSpace(
//    @NotNull(message = "spaceType cannot be null.") SpaceType spaceType,
//    @NotNull(message = "spaceName cannot be null.") String spaceName,
//    @NotNull(message = "openingTime cannot be null.") LocalTime openingTime,
//    @NotNull(message = "closingTime cannot be null.") LocalTime closingTime,
//    @NotNull(message = "hourlyRate cannot be null.") Integer hourlyRate,
//    @NotNull(message = "spaceSize cannot be null.") Integer spaceSize,
//    @NotNull(message = "maxCapacity cannot be null.") Integer maxCapacity,
//    @NotNull(message = "spaceDescription cannot be null.") String spaceDescription,
//    @NotNull(message = "detailedTypes cannot be null.") Set<DetailedType> detailedTypes) {}
