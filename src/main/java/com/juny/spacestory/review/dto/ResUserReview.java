package com.juny.spacestory.review.dto;

import com.juny.spacestory.reservation.dto.ResReservation;

public record ResUserReview(
  String content,
  Double rating,
  ResReservation reservation) {

}
