package com.juny.spacestory.service;

import com.juny.spacestory.domain.Host;
import com.juny.spacestory.domain.Space;
import com.juny.spacestory.domain.SpaceReservation;
import com.juny.spacestory.domain.User;
import com.juny.spacestory.dto.RequestUpdateReservation;
import com.juny.spacestory.dto.TimeSlot;
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

    private static final String SPACE_MINIMUM_ONE_HOUR_MSG = "space reservations require a minimum of 1 hour.";
    private static final String SPACE_ALREAY_RESERVED_MSG = "space is already reserved.";
    private static final String USER_INVALID_MSG = "User is invalid.";
    private static final String SPACE_INVALID_MSG = "Space is invalid.";
    private static final String RESERVATION_INVALID_MSG = "Reservation is invalid.";
    private static final String RESERVATION_REQUEST_INVALID_MSG = "Reservation request is invalid.";

    public SpaceReservation reserve(Long userId, Long spaceId, LocalDate reservationDate, LocalTime start, LocalTime end) {
        User user = findUserById(userId);
        Space space = findSpaceById(spaceId);
        long usageTime = Duration.between(start, end).toHours();

        if (usageTime < 1)
            throw new IllegalArgumentException(SPACE_MINIMUM_ONE_HOUR_MSG);
        if (!isReservationAvailable(spaceId, reservationDate, start, end))
            throw new IllegalArgumentException(SPACE_ALREAY_RESERVED_MSG);

        long usageFee = space.getHourlyRate() * usageTime;
        processPayment(user, space.getRealEstate().getHost(), usageFee);

        return reservationRepository.save(new SpaceReservation(userId, reservationDate, start, end, usageFee, true, space));
    }

    private void processPayment(User user, Host host, long usageFee) {
        user.payFee(usageFee, host);
        userRepository.save(user);
        hostRepository.save(host);
    }

    private boolean isReservationAvailable(Long spaceId, LocalDate reservationDate, LocalTime reqStart, LocalTime reqEnd) {
        List<SpaceReservation> validReservations = reservationRepository.findBySpaceIdAndReservationDateAndIsReservedTrue(spaceId, reservationDate);
        for (var e : validReservations) {
            if (reqStart.isBefore(e.getEndTime()) && reqEnd.isAfter(e.getStartTime())) { // req start ~ req end 사이에 예약이 있다면 안된다.
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

    public List<SpaceReservation> getReservationsByUserId(Long userId) {
        findUserById(userId);

        return reservationRepository.findByUserId(userId);
    }

    public SpaceReservation update(Long userId, Long spaceId, Long reservationId, RequestUpdateReservation req) {
        User user = findUserById(userId);
        Space space = findSpaceById(spaceId);
        SpaceReservation reservation = findReservationById(reservationId);

        List<TimeSlot> availableSlots = calculateAvailableSlots(spaceId, req, reservation);

        calculateAvailableSlots(req, availableSlots);

        reservation.updateReservation(req, user, space.getRealEstate().getHost());

        return reservationRepository.save(reservation);
    }

    private void calculateAvailableSlots(RequestUpdateReservation req, List<TimeSlot> availableSlots) {
        for (LocalTime time = req.startTime(); time.isBefore(req.endTime()); time = time.plusHours(1)) {
            TimeSlot reqSlot = new TimeSlot(time, time.plusHours(1));
            if (!availableSlots.contains(reqSlot)) {
                throw new IllegalArgumentException(RESERVATION_REQUEST_INVALID_MSG);
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
        return reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException(RESERVATION_INVALID_MSG));
    }

    private Space findSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException(SPACE_INVALID_MSG));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(USER_INVALID_MSG));
    }

    public void delete(Long reservationId) {
        SpaceReservation spaceReservation = findReservationById(reservationId);

        reservationRepository.delete(spaceReservation);
    }
}
