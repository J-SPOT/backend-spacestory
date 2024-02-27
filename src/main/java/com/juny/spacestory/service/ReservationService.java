package com.juny.spacestory.service;

import com.juny.spacestory.domain.Host;
import com.juny.spacestory.domain.Space;
import com.juny.spacestory.domain.SpaceReservation;
import com.juny.spacestory.domain.User;
import com.juny.spacestory.dto.RequestCreateReservation;
import com.juny.spacestory.dto.RequestUpdateReservation;
import com.juny.spacestory.dto.ResponseReservation;
import com.juny.spacestory.dto.TimeSlot;
import com.juny.spacestory.exception.global.ErrorCode;
import com.juny.spacestory.exception.space.SpaceInvalidIdException;
import com.juny.spacestory.exception.spaceReservation.ReservationInvalidIdException;
import com.juny.spacestory.exception.spaceReservation.ReservationMinimumTimeException;
import com.juny.spacestory.exception.spaceReservation.ReservationOverlappedTimeException;
import com.juny.spacestory.exception.user.UserUnAuthorizedModifyException;
import com.juny.spacestory.exception.user.UserInvalidIdException;
import com.juny.spacestory.mapper.ReservationMapper;
import com.juny.spacestory.repository.HostRepository;
import com.juny.spacestory.repository.ReservationRepository;
import com.juny.spacestory.repository.SpaceRepository;
import com.juny.spacestory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final UserRepository userRepository;

    private final HostRepository hostRepository;

    private final SpaceRepository spaceRepository;

    private final ReservationRepository reservationRepository;

    private final ReservationMapper mapper;

    public ResponseReservation reserve(Long spaceId, RequestCreateReservation req) {
        User user = findUserById(req.userId());
        Space space = findSpaceById(spaceId);
        long usageTime = Duration.between(req.startTime(), req.endTime()).toHours();

        if (usageTime < 1) {
            throw new ReservationMinimumTimeException(ErrorCode.RESERVATION_MINIMUM_TIME);
        }
        if (!isReservationAvailable(spaceId, req.reservationDate(), req.startTime(), req.endTime())) {
            throw new ReservationOverlappedTimeException(ErrorCode.RESERVATION_OVERLAPPED_TIME);
        }

        long usageFee = space.getHourlyRate() * usageTime;
        if (req.isUser()) {
            processPayment(user, space.getRealEstate().getHost(), usageFee);
        }
        SpaceReservation savedReservation = reservationRepository.save(new SpaceReservation(req.userId(), req.reservationDate(), req.startTime(), req.endTime(), usageFee, true, true, space));
        return mapper.ReservationToResponseCreateReservation(savedReservation);
    }

    private void processPayment(User user, Host host, long usageFee) {
        user.payFee(usageFee, host);
        userRepository.save(user);
        hostRepository.save(host);
    }

    private boolean isReservationAvailable(Long spaceId, LocalDate reservationDate, LocalTime reqStart, LocalTime reqEnd) {
        List<SpaceReservation> validReservations = reservationRepository.findBySpaceIdAndReservationDateAndIsReservedTrue(spaceId, reservationDate);
        for (var e : validReservations) {
            if (reqStart.isBefore(e.getEndTime()) && reqEnd.isAfter(e.getStartTime())) {
                return false;
            }
        }
        return true;
    }

    public List<TimeSlot> getAvailableReservation(Long spaceId, LocalDate reservationDate) {
        Space space = findSpaceById(spaceId);
        List<SpaceReservation> reservedSpace = reservationRepository.findBySpaceIdAndReservationDateAndIsReservedTrue(spaceId, reservationDate);
        LocalTime openingTime = space.getOpeningTime();
        LocalTime closingTime = space.getClosingTime();
        List<TimeSlot> availableSlots = new ArrayList<>();

        for (LocalTime time = openingTime; time.isBefore(closingTime); time = time.plusHours(1)) {
            availableSlots.add(new TimeSlot(time, time.plusHours(1)));
        }

        List<TimeSlot> reservedSlots = reservedSpace
                .stream()
                .map(reservation -> new TimeSlot(reservation.getStartTime(),
                        reservation.getEndTime()))
                .toList();

        for (var e : reservedSlots) {
            availableSlots.removeIf(slot -> {
                LocalTime slotStart = slot.startTime();
                LocalTime slotEnd = slot.endTime();
                return (slotStart.isBefore(e.endTime()) && slotEnd.isAfter(e.startTime()));
            });
        }

        return availableSlots;
    }

    public List<ResponseReservation> getReservationsByUserId(Long userId) {
        findUserById(userId);

        List<SpaceReservation> byUserId = reservationRepository.findByUserId(userId);

        return mapper.ReservationsToResponseCreateReservations(byUserId);
    }

    public ResponseReservation update(Long reservationId, RequestUpdateReservation req) {
        User user = findUserById(req.userId());
        Space space = findSpaceById(req.spaceId());
        SpaceReservation reservation = findReservationById(reservationId);
        if (!reservation.getUserId().equals(req.userId())) {
            throw new UserUnAuthorizedModifyException(ErrorCode.USER_UNAUTHORIZED_TO_MODIFY);
        }
        List<TimeSlot> availableSlots = calculateAvailableSlots(req.spaceId(), req, reservation);

        calculateAvailableSlots(req, availableSlots);

        reservation.updateReservation(req, user, space.getRealEstate().getHost());

        SpaceReservation savedReservation = reservationRepository.save(reservation);
        return mapper.ReservationToResponseCreateReservation(savedReservation);
    }

    private void calculateAvailableSlots(RequestUpdateReservation req, List<TimeSlot> availableSlots) {
        for (LocalTime time = req.startTime(); time.isBefore(req.endTime()); time = time.plusHours(1)) {
            TimeSlot reqSlot = new TimeSlot(time, time.plusHours(1));
            if (!availableSlots.contains(reqSlot)) {
                throw new ReservationOverlappedTimeException(ErrorCode.RESERVATION_OVERLAPPED_TIME);
            }
        }
    }

    private List<TimeSlot> calculateAvailableSlots(Long spaceId, RequestUpdateReservation req, SpaceReservation reservation) {
        List<TimeSlot> availableSlots = getAvailableReservation(spaceId, req.reservationDate());
        if (reservation.getReservationDate().equals(req.reservationDate())) {
            for (LocalTime time = reservation.getStartTime(); time.isBefore(reservation.getEndTime()); time = time.plusHours(1)) {
                availableSlots.add(new TimeSlot(time, time.plusHours(1)));
            }
            availableSlots.sort(Comparator.comparing(TimeSlot::startTime));
        }
        return availableSlots;
    }

    private SpaceReservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationInvalidIdException(ErrorCode.RESERVATION_INVALID_ID));
    }

    private Space findSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId).orElseThrow(() -> new SpaceInvalidIdException(ErrorCode.SPACE_INVALID_ID));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserInvalidIdException(ErrorCode.USER_INVALID_ID));
    }

    public void delete(Long reservationId) {
        SpaceReservation reservation = findReservationById(reservationId);
        reservation.softDelete(reservation);
    }
}
