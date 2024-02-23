package com.juny.spacestory.controller;

import com.juny.spacestory.domain.SpaceReservation;
import com.juny.spacestory.dto.RequestCreateReservation;
import com.juny.spacestory.dto.RequestUpdateReservation;
import com.juny.spacestory.dto.TimeSlot;
import com.juny.spacestory.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/spaces/{spaceId}/reservations")
    public ResponseEntity<SpaceReservation> reserve(@PathVariable Long spaceId, @RequestBody RequestCreateReservation req) {
        SpaceReservation reservation = reservationService.reserve(req.userId(), spaceId, req.reservationDate(), req.startTime(), req.endTime());
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @GetMapping("/spaces/{spaceId}/available-times")
    public ResponseEntity<List<TimeSlot>> getAvailableTimes(
            @PathVariable Long spaceId,
            @RequestParam LocalDate reservationDate) {
        List<TimeSlot> availableReservation = reservationService.getAvailableReservation(spaceId, reservationDate);
        return new ResponseEntity<>(availableReservation, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/reservations")
    public ResponseEntity<List<SpaceReservation>> getUserReservations(@PathVariable Long userId) {
        List<SpaceReservation> reservationsByUserId = reservationService.getReservationsByUserId(userId);
        return new ResponseEntity<>(reservationsByUserId, HttpStatus.OK);
    }

    @PatchMapping("/reservations/{reservationId}")
    public ResponseEntity<SpaceReservation> update(
            @PathVariable Long reservationId,
            @RequestBody RequestUpdateReservation req) {
        SpaceReservation updatedReservation = reservationService.update(req.userId(), req.spaceId(), reservationId, req);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> delete(@PathVariable Long reservationId) {
        reservationService.delete(reservationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
