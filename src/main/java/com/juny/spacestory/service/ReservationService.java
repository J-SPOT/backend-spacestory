package com.juny.spacestory.service;

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

    public SpaceReservation reserve(Long userId, Long spaceId, LocalDate reservationDate, LocalTime start, LocalTime end) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("user is invalid."));
        Space space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("space is invalid."));
        long usageTime = Duration.between(start, end).toHours();

        if (usageTime < 1)
            throw new IllegalArgumentException("space reservations require a minimum of 1 hour.");
        if (!isReservationAvailable(spaceId, reservationDate, start, end))
            throw new IllegalArgumentException("space is already reserved.");

        long usageFee = space.getHourlyRate() * usageTime;
        user.payFee(usageFee, space.getRealEstate().getHost());
        userRepository.save(user);
        hostRepository.save(space.getRealEstate().getHost());

        return reservationRepository.save(new SpaceReservation(userId, reservationDate, start, end, usageFee, true, space));
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
        Space space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("space is invalid."));
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

        for (var e : reservedSpace) {
            LocalTime reservedStart = e.getStartTime();
            LocalTime reservedEnd = e.getEndTime();
            availableSlots.removeIf(slot -> {
                LocalTime slotStart = slot.startTime();
                LocalTime slotEnd = slot.endTime();
                return (slotStart.isBefore(reservedEnd) && slotEnd.isAfter(reservedStart));
            });
        }

        return availableSlots;
    }

    public List<SpaceReservation> getReservationsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("user is invalid."));

        return reservationRepository.findByUserId(userId);
    }

    public SpaceReservation update(Long userId, Long spaceId, Long reservationId, RequestUpdateReservation req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("user is invalid."));
        Space space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("space is invalid."));
        SpaceReservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("reservation is invalid."));
        List<TimeSlot> availableSlots = getAvailableReservation(spaceId, req.reservationDate());

        if (reservation.getReservationDate().equals(req.reservationDate())) {
            for (LocalTime time = reservation.getStartTime(); time.isBefore(reservation.getEndTime()); time = time.plusHours(1)) {
                availableSlots.add(new TimeSlot(time, time.plusHours(1)));
            }
            availableSlots.sort(Comparator.comparing(TimeSlot::startTime));
        }

        boolean isReservationAvailable = true;
        for (LocalTime time = req.startTime(); time.isBefore(req.endTime()); time = time.plusHours(1)) {
            TimeSlot reqSlot = new TimeSlot(time, time.plusHours(1));
            if (!availableSlots.contains(reqSlot)) {
                isReservationAvailable = false;
                break;
            }
        }

        if (!isReservationAvailable) {
            throw new IllegalArgumentException("reservation request is invalid.");
        }
        reservation.updateReservation(req);

        return reservationRepository.save(reservation);
    }

    public void delete(Long reservationId) {
        SpaceReservation spaceReservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("reservation is invalid."));

        reservationRepository.delete(spaceReservation);
    }
}
