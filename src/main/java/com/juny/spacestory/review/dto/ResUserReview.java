package com.juny.spacestory.review.dto;

import com.juny.spacestory.reservation.dto.ResReservation;
import java.util.List;

public record ResUserReview(
  String content,
  Double rating,
  List<String> imagePaths,
  ResReservation reservation) {

}
