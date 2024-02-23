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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationService {

    private final UserRepository userRepository;

    private final HostRepository hostRepository;

    private final SpaceRepository spaceRepository;

    private final ReservationRepository reservationRepository;

    public ReservationService(UserRepository userRepository, HostRepository hostRepository, SpaceRepository spaceRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.hostRepository = hostRepository;
        this.spaceRepository = spaceRepository;
        this.reservationRepository = reservationRepository;
    }

    public SpaceReservation reserve(Long userId, Long spaceId, LocalDate reservationDate, LocalTime start, LocalTime end) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유효하지 않는 유저입니다."));
        Space space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("유효하지 않는 공간입니다."));
        long usageTime = Duration.between(start, end).toHours();
        if (usageTime < 1)
            throw new IllegalArgumentException("공간 예약은 최소 1시간입니다.");
        if (!isReservationAvailable(spaceId, reservationDate, start, end))
            throw new IllegalArgumentException("이미 예약된 공간입니다.");
        long usageFee = space.getHourlyRate() * usageTime;
        user.payFee(usageFee, space.getRealEstate().getHost());
        userRepository.save(user);
        hostRepository.save(space.getRealEstate().getHost());
        return reservationRepository.save(new SpaceReservation(userId, reservationDate, start, end, usageFee, true, space));
    }

    private boolean isReservationAvailable(Long spaceId, LocalDate reservationDate, LocalTime reqStart, LocalTime reqEnd) {
        List<SpaceReservation> validReservations = reservationRepository.findBySpaceIdAndReservationDateAndIsReServedTrue(spaceId, reservationDate, true);
        for (var e : validReservations) {
            if (reqStart.isBefore(e.getEndTime()) && reqEnd.isAfter(e.getStartTime())) { // req start ~ req end 사이에 예약이 있다면 안된다.
                return false;
            }
        }
        return true;
    }

    public List<TimeSlot> getAvailableReservation(Long spaceId, LocalDate reservationDate) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공간입니다."));
        List<SpaceReservation> reservedSpace = reservationRepository.findBySpaceIdAndReservationDateAndIsReServedTrue(spaceId, reservationDate, true);
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
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유효하지 않는 유저입니다."));
        return reservationRepository.findByUserId(userId);
    }

    public SpaceReservation update(Long userId, Long spaceId, Long reservationId, RequestUpdateReservation req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유효하지 않는 유저입니다."));
        Space space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("유효하지 않는 공간입니다."));
        SpaceReservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 예약정보 입니다."));
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
        if (!isReservationAvailable)
            throw new IllegalArgumentException("유효하지 않은 예약 요청입니다.");
        reservation.updateReservation(req);
        return reservationRepository.save(reservation);
    }

    public void delete(Long reservationId) {
        SpaceReservation spaceReservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 예약정보입니다."));
        reservationRepository.delete(spaceReservation);
    }
}
