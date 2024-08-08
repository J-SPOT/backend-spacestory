package com.juny.spacestory.reservation.mapper;

import com.juny.spacestory.reservation.dto.ResReservation;
import com.juny.spacestory.reservation.entity.Reservation;
import com.juny.spacestory.space.mapper.SpaceMapstruct;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = SpaceMapstruct.class)
public interface ReservationMapper {

  ResReservation toResReservation(Reservation reservation);

  default Page<ResReservation> toResReservation(Page<Reservation> reservations) {
    return reservations.map(this::toResReservation);
  }
}
