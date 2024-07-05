package com.juny.spacestory.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

//  private final UserRepository userRepository;
//
//  private final HostRepository hostRepository;
//
//  private final SpaceRepository spaceRepository;
//
//  private final ReservationRepository reservationRepository;
//
//  private final ReservationMapper mapper;
//
//  public ResponseReservation reserve(Long spaceId, RequestCreateReservation req) {
//    User user = findUserById(req.userId());
//    Space space = findSpaceById(spaceId);
//    long usageTime = Duration.between(req.startTime(), req.endTime()).toHours();
//
//    if (usageTime < 1) {
//      throw new ReservationMinimumTimeBusinessException(ErrorCode.RESERVATION_MINIMUM_TIME);
//    }
//    if (!isReservationAvailable(spaceId, req.reservationDate(), req.startTime(), req.endTime())) {
//      throw new ReservationOverlappedTimeBusinessException(ErrorCode.RESERVATION_OVERLAPPED_TIME);
//    }
//
//    long usageFee = space.getHourlyRate() * usageTime;
//    if (req.isUser()) {
//      processPayment(user, space.getRealEstate().getHost(), usageFee);
//    }
//    SpaceReservation savedReservation =
//        reservationRepository.save(
//            new SpaceReservation(
//                req.userId(),
//                req.reservationDate(),
//                req.startTime(),
//                req.endTime(),
//                usageFee,
//                true,
//                false,
//                space));
//    return mapper.ReservationToResponseReservation(savedReservation);
//  }
//
//  private void processPayment(User user, Host host, long usageFee) {
//    user.payFee(usageFee, host);
//    userRepository.save(user);
//    hostRepository.save(host);
//  }
//
//  private boolean isReservationAvailable(
//      Long spaceId, LocalDate reservationDate, LocalTime reqStart, LocalTime reqEnd) {
//    List<SpaceReservation> validReservations =
//        reservationRepository.findBySpaceIdAndReservationDateAndIsDeletedFalse(
//            spaceId, reservationDate);
//    for (var e : validReservations) {
//      if (reqStart.isBefore(e.getEndTime()) && reqEnd.isAfter(e.getStartTime())) {
//        return false;
//      }
//    }
//    return true;
//  }
//
//  public List<TimeSlot> getAvailableReservation(Long spaceId, LocalDate reservationDate) {
//    Space space = findSpaceById(spaceId);
//    List<SpaceReservation> reservedSpace =
//        reservationRepository.findBySpaceIdAndReservationDateAndIsDeletedFalse(
//            spaceId, reservationDate);
//    LocalTime openingTime = space.getOpeningTime();
//    LocalTime closingTime = space.getClosingTime();
//    List<TimeSlot> availableSlots = new ArrayList<>();
//
//    for (LocalTime time = openingTime; time.isBefore(closingTime); time = time.plusHours(1)) {
//      availableSlots.add(new TimeSlot(time, time.plusHours(1)));
//    }
//
//    List<TimeSlot> reservedSlots =
//        reservedSpace.stream()
//            .map(reservation -> new TimeSlot(reservation.getStartTime(), reservation.getEndTime()))
//            .toList();
//
//    for (var e : reservedSlots) {
//      availableSlots.removeIf(
//          slot -> {
//            LocalTime slotStart = slot.startTime();
//            LocalTime slotEnd = slot.endTime();
//            return (slotStart.isBefore(e.endTime()) && slotEnd.isAfter(e.startTime()));
//          });
//    }
//
//    return availableSlots;
//  }
//
//  public List<ResponseReservation> getReservationsByUserId(UUID userId) {
//    findUserById(userId);
//
//    List<SpaceReservation> byUserId = reservationRepository.findByUserId(userId);
//
//    return mapper.ReservationsToResponseReservations(byUserId);
//  }
//
//  public List<ResponseReservation> getSpaceReservations(Long spaceId, LocalDate reservationDate) {
//    List<SpaceReservation> reservations =
//        reservationRepository.findReservationsBySpaceIdAndDateJPQL(spaceId, reservationDate);
//
//    return mapper.ReservationsToResponseReservations(reservations);
//  }
//
//  public ResponseReservation update(Long reservationId, RequestUpdateReservation req) {
//    User user = findUserById(req.userId());
//    Space space = findSpaceById(req.spaceId());
//    SpaceReservation reservation = findReservationById(reservationId);
//    if (!reservation.getUserId().equals(req.userId())) {
//      throw new UserUnAuthorizedModifyBusinessException(ErrorCode.USER_UNAUTHORIZED_TO_MODIFY);
//    }
//    List<TimeSlot> availableSlots = calculateAvailableSlots(req.spaceId(), req, reservation);
//
//    calculateAvailableSlots(req, availableSlots);
//
//    reservation.updateReservation(req, user, space.getRealEstate().getHost());
//
//    SpaceReservation savedReservation = reservationRepository.save(reservation);
//    return mapper.ReservationToResponseReservation(savedReservation);
//  }
//
//  private void calculateAvailableSlots(
//      RequestUpdateReservation req, List<TimeSlot> availableSlots) {
//    for (LocalTime time = req.startTime(); time.isBefore(req.endTime()); time = time.plusHours(1)) {
//      TimeSlot reqSlot = new TimeSlot(time, time.plusHours(1));
//      if (!availableSlots.contains(reqSlot)) {
//        throw new ReservationOverlappedTimeBusinessException(ErrorCode.RESERVATION_OVERLAPPED_TIME);
//      }
//    }
//  }
//
//  private List<TimeSlot> calculateAvailableSlots(
//      Long spaceId, RequestUpdateReservation req, SpaceReservation reservation) {
//    List<TimeSlot> availableSlots = getAvailableReservation(spaceId, req.reservationDate());
//    if (reservation.getReservationDate().equals(req.reservationDate())) {
//      for (LocalTime time = reservation.getStartTime();
//          time.isBefore(reservation.getEndTime());
//          time = time.plusHours(1)) {
//        availableSlots.add(new TimeSlot(time, time.plusHours(1)));
//      }
//      availableSlots.sort(Comparator.comparing(TimeSlot::startTime));
//    }
//    return availableSlots;
//  }
//
//  public void delete(Long reservationId) {
//    SpaceReservation reservation = findReservationById(reservationId);
//    reservation.softDelete(reservation);
//    reservationRepository.save(reservation);
//  }
//
//  private SpaceReservation findReservationById(Long reservationId) {
//    return reservationRepository
//        .findById(reservationId)
//        .orElseThrow(
//            () -> new ReservationInvalidIdBusinessException(ErrorCode.RESERVATION_INVALID_ID));
//  }
//
//  private Space findSpaceById(Long spaceId) {
//    return spaceRepository
//        .findById(spaceId)
//        .orElseThrow(() -> new SpaceInvalidIdBusinessException(ErrorCode.SPACE_INVALID_ID));
//  }
//
//  private User findUserById(UUID userId) {
//    return userRepository
//        .findById(userId)
//        .orElseThrow(() -> new UserInvalidIdBusinessException(ErrorCode.USER_INVALID_ID));
//  }
}
