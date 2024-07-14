package com.juny.spacestory.reservation.controller;

import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import com.juny.spacestory.reservation.dto.ReqReservation;
import com.juny.spacestory.reservation.dto.ResReservation;
import com.juny.spacestory.reservation.dto.TimeSlot;
import com.juny.spacestory.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "특정 공간, 특정일에 예약할 수 있는 시간 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "예약 가능한 시간 조회 성공"),
    })

  @GetMapping("/api/v1/spaces/{id}/available-times")
  public ResponseEntity<List<TimeSlot>> findAvailableTimes(
      @PathVariable Long id, @RequestParam LocalDate reservationDate) {

    List<TimeSlot> availableReservation =
        reservationService.findAvailableReservationTimesBySpaceId(id, reservationDate);

    return new ResponseEntity<>(availableReservation, HttpStatus.OK);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "예약 목록 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/user/reservations")
  public ResponseEntity<Page<ResReservation>> findReservationsByUserId(
    @RequestParam(required = false, defaultValue = "0") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    Page<ResReservation> reservations = reservationService.findReservationsByUserId(
      UUID.fromString(customUserDetails.getId()), page, size);

    return new ResponseEntity<>(reservations, HttpStatus.OK);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "특정 공간, 특정일에 예약 조회 관리자 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "관리자 예약 목록 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/admin/v1/spaces/{id}/reservations")
  public ResponseEntity<Page<ResReservation>> getSpaceReservations(
    @PathVariable Long id, @RequestParam LocalDate reservationDate,
    @RequestParam(required = false, defaultValue = "0") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Page<ResReservation> reservations =
      reservationService.findReservationsBySpaceIdAndReservationDate(id, reservationDate, page, size);

    return new ResponseEntity<>(reservations, HttpStatus.OK);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "특정 공간, 특정일에 단건 예약 조회 관리자 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "관리자 단건 예약 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/admin/v1/spaces/{id}/reservations/{reservationId}")
  public ResponseEntity<ResReservation> findReservationById(
    @PathVariable Long id, @PathVariable Long reservationId) {

    ResReservation reservation = reservationService.findReservationById(id, reservationId);

    return new ResponseEntity<>(reservation, HttpStatus.OK);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "특정 공간, 특정일 및 특정 시간에 예약 추가 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "예약 추가 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 예약 단위 시간이 1시간 미만인 경우<br>400, 이미 예약된 시간에 예약한 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U2",
        description = "400, 예약하려는 유저의 포인트가 충분하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/spaces/{id}/reservations")
  public ResponseEntity<ResReservation> reserveByUser(
      @PathVariable Long id, @RequestBody ReqReservation req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResReservation reservation = reservationService.createReservation(
      UUID.fromString(customUserDetails.getId()), id, req);

    return new ResponseEntity<>(reservation, HttpStatus.CREATED);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "예약 수정 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "예약 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 예약 단위 시간이 1시간 미만인 경우<br>400, 이미 예약된 시간에 예약한 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U2",
        description = "400, 예약하려는 유저의 포인트가 충분하지 않은 경우<br>400, 환불 시 호스트의 포인트가 충분하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PutMapping("/api/v1/spaces/{id}/reservations/{reservationId}")
  public ResponseEntity<ResReservation> updateReservationByAdmin(
    @PathVariable Long id,
    @PathVariable Long reservationId,
    @RequestBody ReqReservation req) {

    ResReservation updatedReservation = reservationService.updateReservationByAdmin(id, reservationId, req);

    return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
  }


  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "예약 수정 관리자 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "예약 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 예약 단위 시간이 1시간 미만인 경우<br>400, 이미 예약된 시간에 예약한 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U2",
        description = "400, 예약하려는 유저의 포인트가 충분하지 않은 경우<br>400, 환불 시 호스트의 포인트가 충분하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PutMapping("/api/admin/v1/spaces/{id}/reservations/{reservationId}")
  public ResponseEntity<ResReservation> updateReservationByUser(
    @PathVariable Long id,
    @PathVariable Long reservationId,
    @RequestBody ReqReservation req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResReservation updatedReservation = reservationService.updateReservation(UUID.fromString(customUserDetails.getId()), id, reservationId, req);

    return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "예약 삭제 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U2",
        description = "400, 환불 시 호스트의 포인트가 충분하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/spaces/{id}/reservations/{reservationId}")
  public ResponseEntity<Void> deleteReservationByUser(
    @PathVariable Long id,
    @PathVariable Long reservationId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    reservationService.deleteReservation(
      UUID.fromString(customUserDetails.getId()), id, reservationId);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "예약 삭제 관리자 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "관리자 예약 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U2",
        description = "400, 환불 시 호스트의 포인트가 충분하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/admin/v1/spaces/{id}/reservations/{reservationId}")
  public ResponseEntity<Void> deleteReservationByAdmin(
    @PathVariable Long id,
    @PathVariable Long reservationId) {

    reservationService.deleteReservationByAdmin(id, reservationId);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
