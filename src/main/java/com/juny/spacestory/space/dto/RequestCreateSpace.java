package com.juny.spacestory.space.dto;

import com.juny.spacestory.space.domain.SpaceType;
import com.juny.spacestory.space.domain.DetailedType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Set;

public record RequestCreateSpace(
    @NotNull(message = "spaceType cannot be null.") SpaceType spaceType,
    @NotNull(message = "spaceName cannot be null.") String spaceName,
    @NotNull(message = "openingTime cannot be null.") LocalTime openingTime,
    @NotNull(message = "closingTime cannot be null.") LocalTime closingTime,
    @NotNull(message = "hourlyRate cannot be null.") Integer hourlyRate,
    @NotNull(message = "spaceSize cannot be null.") Integer spaceSize,
    @NotNull(message = "maxCapacity cannot be null.") Integer maxCapacity,
    @NotNull(message = "spaceDescription cannot be null.") String spaceDescription,
    @NotNull(message = "isDeleted cannot be null.") Boolean isDeleted,
    @NotNull(message = "detailedTypes cannot be null.") Set<DetailedType> detailedTypes,
    @NotNull(message = "roadAddress cannot be null.") String roadAddress,
    @NotNull(message = "jibunAddress cannot be null.") String jibunAddress,
    @NotNull(message = "sido cannot be null.") String sido,
    @NotNull(message = "sigungu cannot be null.") String sigungu,
    @NotNull(message = "dong cannot be null.") String dong,
    @NotNull(message = "floor cannot be null.") Integer floor,
    @NotNull(message = "hasParking cannot be null.") Boolean hasParking,
    @NotNull(message = "hasElevator cannot be null.") Boolean hasElevator,
    @NotNull(message = "hostId cannot be null.") Long hostId) {}
