package com.juny.spacestory.reservation.controller;

import com.juny.spacestory.reservation.dto.RequestCreateReservation;
import com.juny.spacestory.reservation.dto.RequestUpdateReservation;
import com.juny.spacestory.reservation.dto.ResponseReservation;
import com.juny.spacestory.reservation.dto.TimeSlot;
import com.juny.spacestory.reservation.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
public class ReservationController {

  private final ReservationService reservationService;

  public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  @GetMapping("/v1/spaces/{spaceId}/available-times")
  public ResponseEntity<List<TimeSlot>> getAvailableTimes(
      @PathVariable Long spaceId, @RequestParam LocalDate reservationDate) {
    List<TimeSlot> availableReservation =
        reservationService.getAvailableReservation(spaceId, reservationDate);

    return new ResponseEntity<>(availableReservation, HttpStatus.OK);
  }

  @GetMapping("/v1/users/{userId}/reservations")
  public ResponseEntity<List<ResponseReservation>> getUserReservations(@PathVariable Long userId) {
    List<ResponseReservation> reservations = reservationService.getReservationsByUserId(userId);

    return new ResponseEntity<>(reservations, HttpStatus.OK);
  }

  @GetMapping("/v1/spaces/{spaceId}/reservations")
  public ResponseEntity<List<ResponseReservation>> getSpaceReservations(
      @PathVariable Long spaceId, @RequestParam LocalDate reservationDate) {
    List<ResponseReservation> reservations =
        reservationService.getSpaceReservations(spaceId, reservationDate);

    return new ResponseEntity<>(reservations, HttpStatus.OK);
  }

  @PostMapping("/v1/spaces/{spaceId}/reservations")
  public ResponseEntity<ResponseReservation> reserve(
      @PathVariable Long spaceId, @RequestBody RequestCreateReservation req) {
    ResponseReservation reservation = reservationService.reserve(spaceId, req);

    return new ResponseEntity<>(reservation, HttpStatus.CREATED);
  }

  @PatchMapping("/v1/reservations/{reservationId}")
  public ResponseEntity<ResponseReservation> update(
      @PathVariable Long reservationId, @RequestBody RequestUpdateReservation req) {
    ResponseReservation updatedReservation = reservationService.update(reservationId, req);

    return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
  }

  @DeleteMapping("/v1/reservations/{reservationId}")
  public ResponseEntity<Void> delete(@PathVariable Long reservationId) {
    reservationService.delete(reservationId);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
