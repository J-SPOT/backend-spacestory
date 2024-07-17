package com.juny.spacestory.reservation.service;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.reservation.dto.ReqReservation;
import com.juny.spacestory.reservation.dto.ResRefunds;
import com.juny.spacestory.reservation.dto.ResReservation;
import com.juny.spacestory.reservation.dto.TimeSlot;
import com.juny.spacestory.reservation.entity.Reservation;
import com.juny.spacestory.reservation.entity.ReservationStatus;
import com.juny.spacestory.reservation.mapper.ReservationMapper;
import com.juny.spacestory.reservation.repository.ReservationRepository;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.repository.SpaceRepository;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
  private final String RESERVATION_STATUS_NOT_PENDING = "Reservation status not pending";

  public List<TimeSlot> findAvailableReservationTimesBySpaceId(Long spaceId, LocalDate reservationDate) {
    Space space = findSpaceById(spaceId);
    List<Reservation> reservations =
      reservationRepository.findActiveReservations(spaceId, reservationDate);
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
      reservationRepository.findByUserIdAndDeletedAtIsNull(uuid, PageRequest.of(page, size, sort));

    return mapper.toResReservation(reservations);
  }

  public Page<ResReservation> findReservationsBySpaceIdAndReservationDate(
    Long spaceId, LocalDate reservationDate, int page, int size) {

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
  public ResReservation createReservation(UUID userId, Long spaceId, ReqReservation req) {

    User user = findUserById(userId);
    Space space = findSpaceById(spaceId);
    User host = findUserById(space.getRealEstate().getUser().getId());

    if (userId.equals(host.getId())) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    long usageFee = getUsageFee(spaceId, req, null, space.getHourlyRate());

    user.payFeeForHost(usageFee);
    host.receivedFee(usageFee);

    Reservation reservation =
      new Reservation(req.reservationDate(), req.startTime(), req.endTime(), usageFee);

    reservation.setUser(user);
    reservation.setSpace(space);

    Reservation savedReservation = reservationRepository.save(reservation);

    return mapper.toResReservation(savedReservation);
  }

  @Transactional
  public void approveReservationByHost(Long spaceId, Long reservationId, UUID hostId) {

    Space space = findSpaceById(spaceId);
    Reservation reservation = findReservationById(reservationId);

    if (!space.getRealEstate().getUser().getId().equals(hostId)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    reservation.approveReservation();
  }

  @Transactional
  public void rejectReservationByHost(Long spaceId, Long reservationId, UUID hostId) {

    Space space = findSpaceById(spaceId);
    Reservation reservation = findReservationById(reservationId);
    User user = findUserById(reservation.getUser().getId());
    User host = findUserById(hostId);

    if (!reservation.getStatus().equals(ReservationStatus.대기)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, RESERVATION_STATUS_NOT_PENDING);
    }

    if (!space.getRealEstate().getUser().getId().equals(hostId)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    user.payFeeForHost(-reservation.getFee());
    host.receivedFee(-reservation.getFee());

    reservationRepository.save(reservation);

    reservation.cancelReservation();
  }

  @Transactional
  public void approveChangedReservationByHost(Long spaceId, Long reservationId, UUID hostId) {

    Space space = findSpaceById(spaceId);
    Reservation newReservation = findReservationById(reservationId);
    Reservation originReservation = findReservationById(newReservation.getOriginReservationId());
    User user = findUserById(newReservation.getUser().getId());
    User host = findUserById(hostId);

    if (!hostId.equals(space.getRealEstate().getUser().getId())) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    long additionalFee = newReservation.getFee() - originReservation.getFee();
    user.payFeeForHost(additionalFee);
    host.payFeeForHost(-additionalFee);

    originReservation.cancelReservation();
    newReservation.approveReservation();
  }

  @Transactional
  public void rejectChangedReservationByHost(Long spaceId, Long reservationId, UUID hostId) {

    Space space = findSpaceById(spaceId);
    Reservation newReservation = findReservationById(reservationId);

    if (!hostId.equals(space.getRealEstate().getUser().getId())) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    newReservation.cancelReservation();
  }

  @Transactional
  public ResReservation updateReservationByUser(UUID userId, Long spaceId, Long reservationId, ReqReservation req) {

    User user = findUserById(userId);
    Space space = findSpaceById(spaceId);
    Reservation reservation = findReservationById(reservationId);

    if (!userId.equals(reservation.getUser().getId())) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    long usageFee = getUsageFee(spaceId, req, reservation, space.getHourlyRate());

    Reservation newReservation =
      new Reservation(req.reservationDate(), req.startTime(), req.endTime(), usageFee, reservationId);

    Reservation savedNewReservation = reservationRepository.save(newReservation);

    savedNewReservation.setSpace(space);

    savedNewReservation.setUser(user);

    return mapper.toResReservation(savedNewReservation);
  }

  @Transactional
  public ResRefunds cancelReservationByUser(UUID userId, Long reservationId) {

    User user = findUserById(userId);
    Reservation reservation = findReservationById(reservationId);
    User host = findUserById(reservation.getSpace().getRealEstate().getUser().getId());

    if (!userId.equals(reservation.getUser().getId())) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    long payback = reservation.getFee();
    double refundRate = 1.0;
    if (isExceedingTwoHours(reservation.getCreatedAt(), LocalDateTime.now())) {
      refundRate = getRefundRate(LocalDate.now(), reservation.getReservationDate());
    }
    payback *= refundRate;
    user.payFeeForHost(-payback);
    host.payFeeForHost(payback);

    reservation.cancelReservation();

    return new ResRefunds(payback);
  }

  private double getRefundRate(LocalDate now, LocalDate reservationDate) {

    long betweenDays = ChronoUnit.DAYS.between(now, reservationDate);

    if (betweenDays >= 8) {
      return 1.0;
    } else if (betweenDays >= 7) {
      return 0.9;
    } else if (betweenDays >= 6) {
      return 0.8;
    } else if (betweenDays >= 5) {
      return 0.7;
    } else if (betweenDays >= 4) {
      return 0.6;
    } else if (betweenDays >= 3) {
      return 0.5;
    } else {
      return 0;
    }
  }

  private boolean isExceedingTwoHours(LocalDateTime createdAt, LocalDateTime now) {

    System.out.println(
      "ChronoUnit.MINUTES.between(createdAt, now) = " + ChronoUnit.MINUTES.between(createdAt, now));
    return ChronoUnit.MINUTES.between(createdAt, now) > 120;
  }

  @Transactional
  public void deleteReservation(UUID userId, Long reservationId) {

    Reservation reservation = findReservationById(reservationId);

    if (!userId.equals(reservation.getUser().getId())) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    reservation.softDelete();
  }

  private Reservation findReservationById(Long reservationId) {

    return reservationRepository.findById(reservationId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_RESERVATION_ID));
  }

  private long getUsageFee(Long spaceId, ReqReservation req, Reservation reservation, Integer spaceHourlyRate) {

    long usageTime = Duration.between(req.startTime(), req.endTime()).toHours();

    if (usageTime < 1) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, RESERVATION_MINIMUM_TIME);
    }

    if (isReservationConflict(spaceId, req.reservationDate(), req.startTime(), req.endTime(),
      reservation)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, RESERVATION_CONFLICT_TIME);
    }

    return spaceHourlyRate * usageTime;
  }

  private boolean isReservationConflict(
    Long spaceId, LocalDate reservationDate, LocalTime reqStart, LocalTime reqEnd, Reservation originReservation) {

    List<Reservation> validReservations =
      reservationRepository.findActiveReservations(spaceId, reservationDate);

    if (originReservation != null) {
      validReservations.removeIf(reservation -> reservation.getId().equals(originReservation.getId()));
    }

    for (var e : validReservations) {
      if (reqStart.isBefore(e.getEndTime()) && reqEnd.isAfter(e.getStartTime())) {
        return true;
      }
    }
    return false;
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
