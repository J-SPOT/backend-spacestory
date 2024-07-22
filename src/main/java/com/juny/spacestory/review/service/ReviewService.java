package com.juny.spacestory.review.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.reservation.entity.Reservation;
import com.juny.spacestory.reservation.entity.ReservationStatus;
import com.juny.spacestory.reservation.repository.ReservationRepository;
import com.juny.spacestory.review.domain.Review;
import com.juny.spacestory.review.dto.ReqReview;
import com.juny.spacestory.review.dto.ResReview;
import com.juny.spacestory.review.dto.ResUserReview;
import com.juny.spacestory.review.mapper.ReviewMapper;
import com.juny.spacestory.review.repository.ReviewRepository;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;

  private final UserRepository userRepository;

  private final ReviewMapper mapper;

  private final ReservationRepository reservationRepository;

  private final AmazonS3 amazonS3;

  private final String INVALID_RESERVATION_ID = "Invalid reservation id";
  private final String INVALID_RESERVATION_TIME = "Reservation didn't end.";
  private final String INVALID_RESERVATION_STATUS = "Reservation status not '승인'";
  private final String INVALID_USER_ID = "Invalid user id";
  private final String INVALID_REVIEW_ID = "Invalid review id";
  private final String EXCEED_THREE_REVIEW_IMAGES = " Exceed three review images";
  private final String INVALID_IMAGE_PATH = "Invalid image path";

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @Transactional
  public ResReview createReview(Long reservationId, ReqReview req, UUID userId) {

    User user = userRepository.findById(userId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID));

    Reservation reservation = checkValidReservation(reservationId);

    reservation.getSpace();

    Review review = new Review(req.content(), req.rating());

    review.setReservation(reservation);

    review.setUser(user);

    reviewRepository.save(review);

    return mapper.toResReview(review);
  }

  private Reservation checkValidReservation(Long reservationId) {

    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_RESERVATION_ID));

    LocalDateTime now = LocalDateTime.now();

    LocalDateTime reservationTime = LocalDateTime.of(
      reservation.getReservationDate(), reservation.getEndTime());

    if (now.isBefore(reservationTime)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_RESERVATION_TIME);
    }

    if (reservation.getStatus() != ReservationStatus.승인) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_RESERVATION_STATUS);
    }

    return reservation;
  }

  @Transactional
  public Page<ResUserReview> findAllReviewsByUser(UUID userId, int page, int size) {

    Page<Review> reviews = reviewRepository.findAllByUserIdAndDeletedAtIsNull(
      userId, PageRequest.of(page, size));

    return mapper.toResUserReviews(reviews);
  }

  public Page<ResReview> findAllReviewsBySpaceId(Long spaceId, int page, int size) {

    Page<Review> reviews = reviewRepository.findReviewsBySpaceId(spaceId,
      PageRequest.of(page, size));

    return mapper.toResReviews(reviews);
  }

  @Transactional
  public ResUserReview updateReviewByUser(Long reviewId, ReqReview req, UUID userId) {

    Review review = reviewRepository.findById(reviewId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REVIEW_ID));

    if (!review.getUser().getId().equals(userId)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    review.update(req);

    return mapper.toResUserReview(review);
  }

  @Transactional
  public void deleteReviewByUser(Long reviewId, UUID userId) {

    Review review = reviewRepository.findById(reviewId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REVIEW_ID));

    if (!review.getUser().getId().equals(userId)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    review.softDelete();
  }

  @Transactional
  public ResReview uploadImages(Long reviewId, UUID userId, List<MultipartFile> files)
    throws IOException {

    Review review = reviewRepository.findById(reviewId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REVIEW_ID));

    if (!review.getUser().getId().equals(userId)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    if (review.getImagePaths().size() + files.size() > 3) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, EXCEED_THREE_REVIEW_IMAGES);
    }

    for (var file : files) {
      String original = file.getOriginalFilename();
      String unique = UUID.randomUUID().toString() + "-" + original;

      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(file.getSize());
      amazonS3.putObject(bucketName, unique, file.getInputStream(), metadata);

      review.getImagePaths().add(unique);
    }

    return mapper.toResReview(review);
  }

  public void deleteImage(Long reviewId, UUID userId, String imagePath) {

    Review review = reviewRepository.findById(reviewId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REVIEW_ID));

    if (!review.getUser().getId().equals(userId)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    if (!review.getImagePaths().contains(imagePath)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_IMAGE_PATH);
    }

    amazonS3.deleteObject(bucketName, imagePath);
    review.getImagePaths().remove(imagePath);
  }
}
