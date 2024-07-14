package com.juny.spacestory.reservation.service;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.reservation.dto.ReqReservation;
import com.juny.spacestory.reservation.dto.ResReservation;
import com.juny.spacestory.reservation.dto.TimeSlot;
import com.juny.spacestory.reservation.entity.Reservation;
import com.juny.spacestory.reservation.mapper.ReservationMapper;
import com.juny.spacestory.reservation.repository.ReservationRepository;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.repository.SpaceRepository;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final UserRepository userRepository;

  private final SpaceRepository spaceRepository;

  private final ReservationRepository reservationRepository;

  private final ReservationMapper mapper;

  private final String INVALID_SPACE_ID = "Invalid space id";
  private final String INVALID_USER_ID = "Invalid user id";
  private final String INVALID_RESERVATION_ID = "Invalid reservation id";
  private final String RESERVATION_MINIMUM_TIME = "At least one hour";
  private final String RESERVATION_CONFLICT_TIME = "There is a reservation scheduling conflict";

  public List<TimeSlot> findAvailableReservationTimesBySpaceId(Long spaceId, LocalDate reservationDate) {
    Space space = findSpaceById(spaceId);
    List<Reservation> reservations =
      reservationRepository.findBySpaceIdAndReservationDate(spaceId, reservationDate);
    LocalTime openingTime = space.getOpeningTime();
    LocalTime closingTime = space.getClosingTime();
    List<TimeSlot> availableSlots = new ArrayList<>();

    for (LocalTime time = openingTime; time.isBefore(closingTime); time = time.plusHours(1)) {
      availableSlots.add(new TimeSlot(time, time.plusHours(1)));
    }

    List<TimeSlot> reservedSlots =
        reservations.stream()
            .map(reservation -> new TimeSlot(reservation.getStartTime(), reservation.getEndTime()))
            .toList();

    for (var reservedSlot : reservedSlots) {
      availableSlots.removeIf(
          availableSlot -> {
            LocalTime availableSlotStartTime = availableSlot.startTime();
            LocalTime availableSlotEndTime = availableSlot.endTime();
            return (availableSlotStartTime.isBefore(reservedSlot.endTime()) && availableSlotEndTime.isAfter(reservedSlot.startTime()));
          });
    }

    return availableSlots;
  }

  public Page<ResReservation> findReservationsByUserId(UUID uuid, int page, int size) {
    findUserById(uuid);

    Sort sort = Sort.by(Sort.Order.desc("reservationDate"), Sort.Order.asc("startTime"));

    findUserById(uuid);

    Page<Reservation> reservations =
      reservationRepository.findByUserId(uuid, PageRequest.of(page, size, sort));

    return mapper.toResReservation(reservations);
  }

  public Page<ResReservation> findReservationsBySpaceIdAndReservationDate(Long spaceId, LocalDate reservationDate,
    int page, int size) {

    Sort sort = Sort.by(Sort.Order.desc("reservationDate"), Sort.Order.asc("startTime"));

    Page<Reservation> reservations =
      reservationRepository.findBySpaceIdAndReservationDate(
        spaceId, reservationDate, PageRequest.of(page, size, sort));

    return mapper.toResReservation(reservations);
  }

  public ResReservation findReservationById(Long spaceId, Long reservationId) {

    findSpaceById(spaceId);

    Reservation reservation = findReservationById(reservationId);

    return mapper.toResReservation(reservation);
  }


  @Transactional
  public ResReservation createReservation(UUID uuid, Long spaceId, ReqReservation req) {
    User user = findUserById(uuid);
    Space space = findSpaceById(spaceId);
    User host = findUserById(space.getRealEstate().getUser().getId());

    long usageTime = Duration.between(req.startTime(), req.endTime()).toHours();

    if (usageTime < 1) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, RESERVATION_MINIMUM_TIME);
    }

    if (!isReservationAvailable(spaceId, req.reservationDate(), req.startTime(), req.endTime(), false, null)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, RESERVATION_CONFLICT_TIME);
    }

    long usageFee = space.getHourlyRate() * usageTime;

    user.payFeeForHost(usageFee);
    host.receivedFee(usageFee);

    Reservation reservation =
      new Reservation(req.reservationDate(), req.startTime(), req.endTime(), usageFee);

    reservation.setUser(user);
    reservation.setSpace(space);

    Reservation savedReservation = reservationRepository.save(reservation);

    return mapper.toResReservation(savedReservation);
  }

  private boolean isReservationAvailable(
    Long spaceId, LocalDate reservationDate, LocalTime reqStart, LocalTime reqEnd, boolean isUpdate, Reservation originReservation) {

    List<Reservation> validReservations =
      reservationRepository.findBySpaceIdAndReservationDate(spaceId, reservationDate);

    if (isUpdate && originReservation != null) {
      validReservations.removeIf(reservation -> reservation.getId().equals(originReservation.getId()));
    }

    for (var e : validReservations) {
      if (reqStart.isBefore(e.getEndTime()) && reqEnd.isAfter(e.getStartTime())) {
        return false;
      }
    }
    return true;
  }

  @Transactional
  public ResReservation updateReservation(UUID uuid, Long spaceId, Long reservationId, ReqReservation req) {

    Space space = findSpaceById(spaceId);
    Reservation reservation = findReservationById(reservationId);
    User user = findUserById(uuid);
    User host = findUserById(space.getRealEstate().getUser().getId());

    long usageTime = Duration.between(req.startTime(), req.endTime()).toHours();

    if (usageTime < 1) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, RESERVATION_MINIMUM_TIME);
    }

    if (!isReservationAvailable(spaceId, req.reservationDate(), req.startTime(), req.endTime(), true, reservation)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, RESERVATION_CONFLICT_TIME);
    }

    long usageFee = space.getHourlyRate() * usageTime;

    user.payFeeForHost(-reservation.getFee() + usageFee);
    host.receivedFee(-reservation.getFee() + usageFee);

    reservation.updateReservation(req, usageFee);

    return mapper.toResReservation(reservation);
  }

  @Transactional
  public ResReservation updateReservationByAdmin(
    Long spaceId, Long reservationId, ReqReservation req) {

    Space space = findSpaceById(spaceId);
    Reservation reservation = findReservationById(reservationId);
    User user = findUserById(reservation.getUser().getId());
    User host = findUserById(space.getRealEstate().getUser().getId());

    long usageTime = Duration.between(req.startTime(), req.endTime()).toHours();

    if (usageTime < 1) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, RESERVATION_MINIMUM_TIME);
    }

    if (!isReservationAvailable(spaceId, req.reservationDate(), req.startTime(), req.endTime(), true, reservation)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, RESERVATION_CONFLICT_TIME);
    }

    long usageFee = space.getHourlyRate() * usageTime;

    user.payFeeForHost(-reservation.getFee() + usageFee);
    host.receivedFee(-reservation.getFee() + usageFee);

    reservation.updateReservation(req, usageFee);

    return mapper.toResReservation(reservation);
  }

  public void deleteReservation(UUID uuid, Long spaceId, Long reservationId) {
    Space space = findSpaceById(spaceId);
    Reservation reservation = findReservationById(reservationId);
    User user = findUserById(uuid);
    User host = findUserById(space.getRealEstate().getUser().getId());

    user.payFeeForHost(-reservation.getFee());
    host.receivedFee(-reservation.getFee());

    reservationRepository.delete(reservation);
  }

  public void deleteReservationByAdmin(Long spaceId, Long reservationId) {

    Space space = findSpaceById(spaceId);
    Reservation reservation = findReservationById(reservationId);
    User user = findUserById(reservation.getUser().getId());
    User host = findUserById(space.getRealEstate().getUser().getId());

    user.payFeeForHost(-reservation.getFee());
    host.receivedFee(-reservation.getFee());

    reservationRepository.delete(reservation);
  }


  private Reservation findReservationById(Long reservationId) {

    return reservationRepository.findById(reservationId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_RESERVATION_ID));
  }

  private User findUserById(UUID userId) {

    return userRepository
      .findById(userId)
      .orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID));
  }

  private Space findSpaceById(Long spaceId) {

    return spaceRepository
      .findById(spaceId)
      .orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID));
  }
}
