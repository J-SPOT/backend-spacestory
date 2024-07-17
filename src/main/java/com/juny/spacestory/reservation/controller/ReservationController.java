package com.juny.spacestory.reservation.controller;

import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import com.juny.spacestory.reservation.dto.ReqReservation;
import com.juny.spacestory.reservation.dto.ResRefunds;
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
  @Operation(summary = "특정 공간, 특정일에 예약할 수 있는 시간 조회 API", description = "호스트가 아직 승인하지 않은 예약 시간도 고려하여 예약 가능한 시간을 반환합니다.")
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
  @Operation(summary = "특정 공간, 특정일 및 특정 시간에 예약 추가 API",description = "예약은 <대기, 승인, 취소> 상태를 가집니다.<br> 예약은 <대기> 상태로 만들어지며, 호스트가 승인하거나 거절하여 <승인> 또는 <취소> 상태가 됩니다. <br> 호스트가 예약 거절할 경우 포인트가 100% 반환됩니다. <br><br>예약일 2일전부터 환불액 없음 <br>예약일 3일전에 50%, 4일전 60%, 5일전 70%, 6일전 80%, 7일전 90%, 8일전 100%로 환불 <br>예약 신청 후 2시간 이내 취소시 100% 환불")
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
  @Operation(summary = "호스트 예약 승인 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "예약 추가 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/spaces/{id}/reservations/{reservationId}/approve")
  public ResponseEntity<Void> approveReservation(
    @PathVariable Long id, @PathVariable Long reservationId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    reservationService.approveReservationByHost(
      id, reservationId, UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "호스트 예약 거절 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "호스트 예약 거절 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U2",
        description = "400, 호스트가 환불할 금액이 부족한 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/spaces/{id}/reservations/{reservationId}/reject")
  public ResponseEntity<Void> rejectReservation(
    @PathVariable Long id, @PathVariable Long reservationId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    reservationService.rejectReservationByHost(
      id, reservationId, UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "유저 예약 수정 API", description = "수정된 내용으로 새로운 예약을 만듭니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "수정된 내용으로 예약 생성 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 예약 단위 시간이 1시간 미만인 경우<br>400, 이미 예약된 시간에 예약한 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U2",
        description = "400, 예약하려는 유저의 포인트가 충분하지 않은 경우<br>400, 환불 시 호스트의 포인트가 충분하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/spaces/{id}/reservations/{reservationId}/request-change")
  public ResponseEntity<ResReservation> updateReservationByUser(
    @PathVariable Long id,
    @PathVariable Long reservationId,
    @RequestBody ReqReservation req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResReservation newReservation = reservationService.updateReservationByUser(UUID.fromString(customUserDetails.getId()), id, reservationId, req);

    return new ResponseEntity<>(newReservation, HttpStatus.OK);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "호스트 수정된 예약 승인 API", description = "기존 예약을 취소하고, 새로운 예약을 승인합니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "수정된 예약 승인 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 예약 단위 시간이 1시간 미만인 경우<br>400, 이미 예약된 시간에 예약한 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U2",
        description = "400, 예약하려는 유저의 포인트가 충분하지 않은 경우<br>400, 환불 시 호스트의 포인트가 충분하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
  @PatchMapping("/api/v1/spaces/{id}/reservations/{reservationId}/approve-change")
  public ResponseEntity<Void> approveChangedReservation(
    @PathVariable Long id, @PathVariable Long reservationId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    reservationService.approveChangedReservationByHost(id, reservationId, UUID.fromString(
      customUserDetails.getId()));


    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "호스트 수정된 예약 거절 API", description = "새로운 예약을 취소합니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "수정된 예약 거절 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 예약 단위 시간이 1시간 미만인 경우<br>400, 이미 예약된 시간에 예약한 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/spaces/{id}/reservations/{reservationId}/reject-change")
  public ResponseEntity<Void> rejectChangedReservation(
    @PathVariable Long id, @PathVariable Long reservationId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    reservationService.rejectChangedReservationByHost(id, reservationId, UUID.fromString(
      customUserDetails.getId()));


    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "유저 예약 취소 API", description = "예약일 2일전부터 환불액 없음 <br>예약일 3일전에 50%, 4일전 60%, 5일전 70%, 6일전 80%, 7일전 90%, 8일전 100%로 환불 <br>예약 신청 후 2시간 이내 취소시 100% 환불")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "환불액 반환 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U2",
        description = "400, 환불 시 호스트의 포인트가 충분하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/user/reservations/{reservationId}/cancel")
  public ResponseEntity<ResRefunds> cancelReservationsByUser(
    @PathVariable Long reservationId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResRefunds refunds = reservationService.cancelReservationByUser(
      UUID.fromString(customUserDetails.getId()), reservationId);

    return new ResponseEntity<>(refunds, HttpStatus.OK);
  }

  @Tag(name = "예약 API", description = "예약 조회, 예약 추가, 예약 수정, 예약 삭제")
  @Operation(summary = "예약 삭제 API", description = "유저가 자신의 예약 목록에 있는 예약을 삭제합니다. 실제로 예약이 삭제되지 않습니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
  @DeleteMapping("/api/v1/user/reservations/{reservationId}")
  public ResponseEntity<Void> deleteReservationByUser(
    @PathVariable Long reservationId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    reservationService.deleteReservation(
      UUID.fromString(customUserDetails.getId()), reservationId);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
