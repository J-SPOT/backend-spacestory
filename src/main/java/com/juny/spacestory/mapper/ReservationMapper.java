package com.juny.spacestory.mapper;

import com.juny.spacestory.domain.SpaceReservation;
import com.juny.spacestory.dto.ResponseReservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(source = "id", target = "reservationId")
    @Mapping(source = "reservationDate", target = "reservationDate")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    @Mapping(source = "fee", target = "fee")
    @Mapping(source = "space.spaceType", target = "spaceType")
    @Mapping(source = "space.spaceName", target = "spaceName")
    @Mapping(source = "space.openingTime", target = "openingTime")
    @Mapping(source = "space.closingTime", target = "closingTime")
    @Mapping(source = "space.hourlyRate", target = "hourlyRate")
    @Mapping(source = "space.spaceSize", target = "spaceSize")
    @Mapping(source = "space.maxCapacity", target = "maxCapacity")
    @Mapping(source = "space.detailedTypes", target = "detailedTypes")
    @Mapping(source = "space.realEstate.floor", target = "floor")
    @Mapping(source = "space.realEstate.hasParking", target = "hasParking")
    @Mapping(source = "space.realEstate.hasElevator", target = "hasElevator")
    ResponseReservation ReservationToResponseCreateReservation(SpaceReservation reservation);

    List<ResponseReservation> ReservationsToResponseCreateReservations(List<SpaceReservation> reservations);
}