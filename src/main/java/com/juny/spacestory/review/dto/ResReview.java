package com.juny.spacestory.review.dto;

import java.util.List;

public record ResReview(
  String content,
  Double rating,
  List<String> imagePaths,
  String email) {

}
