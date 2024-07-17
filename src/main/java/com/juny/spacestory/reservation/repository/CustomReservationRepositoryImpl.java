package com.juny.spacestory.reservation.repository;

import static com.juny.spacestory.reservation.entity.QReservation.reservation;

import com.juny.spacestory.reservation.entity.Reservation;
import com.juny.spacestory.reservation.entity.ReservationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomReservationRepositoryImpl implements CustomReservationRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Reservation> findActiveReservations(Long spaceId, LocalDate reservationDate) {

    return queryFactory.selectFrom(reservation)
      .where(reservation.space.id.eq(spaceId)
        .and(reservation.reservationDate.eq(reservationDate))
        .and(reservation.status.ne(ReservationStatus.취소)))
      .fetch();
  }
}
