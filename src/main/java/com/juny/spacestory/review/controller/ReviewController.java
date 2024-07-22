package com.juny.spacestory.review.controller;

import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import com.juny.spacestory.review.dto.ReqReview;
import com.juny.spacestory.review.dto.ResReview;
import com.juny.spacestory.review.dto.ResUserReview;
import com.juny.spacestory.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @Tag(name = "리뷰 API", description = "리뷰 조회, 리뷰 추가, 리뷰 수정, 리뷰 삭제")
  @Operation(summary = "리뷰 추가 API", description = "예약마다 하나의 리뷰를 작성할 수 있습니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "리뷰 추가 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 리뷰 id가 유효하지 않은 경우<br>400, 예약 시간이 지나지 않고 리뷰를 쓰는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/reservations/{id}/reviews")
  public ResponseEntity<ResReview> createReview(@PathVariable Long id, @RequestBody ReqReview req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResReview review = reviewService.createReview(id, req,
      UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(review, HttpStatus.OK);
  }

  @Tag(name = "리뷰 API", description = "리뷰 조회, 리뷰 추가, 리뷰 수정, 리뷰 삭제")
  @Operation(summary = "유저가 작성한 리뷰 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
    })

  @GetMapping("/api/v1/user/reviews")
  public ResponseEntity<Page<ResUserReview>> getReviewsByUser(
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    Page<ResUserReview> reviews = reviewService.findAllReviewsByUser(
      UUID.fromString(customUserDetails.getId()), page - 1, size);

    return new ResponseEntity<>(reviews, HttpStatus.OK);
  }

  @Tag(name = "리뷰 API", description = "리뷰 조회, 리뷰 추가, 리뷰 수정, 리뷰 삭제")
  @Operation(summary = "특정 공간에 대한 리뷰 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
    })

  @GetMapping("/api/v1/spaces/{id}/reviews")
  public ResponseEntity<Page<ResReview>> getReviewsBySpace(
    @PathVariable Long id,
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Page<ResReview> reviews = reviewService.findAllReviewsBySpaceId(id, page - 1, size);

    return new ResponseEntity<>(reviews, HttpStatus.OK);
  }

  @Tag(name = "리뷰 API", description = "리뷰 조회, 리뷰 추가, 리뷰 수정, 리뷰 삭제")
  @Operation(summary = "리뷰 수정 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 리뷰 id가 유효하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PutMapping("/api/v1/user/reviews/{id}")
  public ResponseEntity<ResUserReview> updateReviewByUser(
    @PathVariable Long id, @RequestBody ReqReview req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResUserReview review = reviewService.updateReviewByUser(id, req, UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(review, HttpStatus.OK);
  }

  @Tag(name = "리뷰 API", description = "리뷰 조회, 리뷰 추가, 리뷰 수정, 리뷰 삭제")
  @Operation(summary = "리뷰 삭제 API", description = "삭제한 review는 조회되지 않습니다. 관리자만 해당 review를 조회할 수 있습니다")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 리뷰 id가 유효하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/user/reviews/{id}")
  public ResponseEntity<Void> deleteReviewByUser(@PathVariable Long id) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    reviewService.deleteReviewByUser(id, UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "리뷰 API", description = "리뷰 조회, 리뷰 추가, 리뷰 수정, 리뷰 삭제")
  @Operation(
    summary = "리뷰 이미지 추가 API",
    description = "리뷰마다 최대 3개의 이미지를 올릴 수 있습니다.<br>Content-Type: multipart/form-data<br>이미지 업로드, 삭제 API는 이미지 경로를 모두 요청 파라미터로 받습니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "리뷰 이미지 추가 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 유효하지 않은 리뷰 아이디인 경우, <br>400, 이미지를 3개 초과해서 업로드 하려는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/user/reviews/{id}/images")
  public ResponseEntity<ResReview> uploadImages(
    @PathVariable Long id,
    @RequestParam List<MultipartFile> files) throws IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResReview review = reviewService.uploadImages(id, UUID.fromString(customUserDetails.getId()), files);

    return new ResponseEntity<>(review, HttpStatus.OK);
  }

  @Tag(name = "리뷰 API", description = "리뷰 조회, 리뷰 추가, 리뷰 수정, 리뷰 삭제")
  @Operation(
    summary = "리뷰 이미지 단건 삭제 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "리뷰 이미지 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 유효하지 않은 리뷰 아이디인 경우, <br>400, 유효하지 않은 이미지 경로인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/user/reviews/{id}/image")
  public ResponseEntity<Void> deleteImage(
    @PathVariable Long id,
    @RequestParam String imagePath) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    reviewService.deleteImage(id, UUID.fromString(customUserDetails.getId()), imagePath);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
