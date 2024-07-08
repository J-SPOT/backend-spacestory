package com.juny.spacestory.space.dto;

import java.time.LocalTime;

public record ResSpace(
  Long id,
  String spaceName,
  LocalTime openingTime,
  LocalTime closingTime,
  Integer hourlyRate,
  Integer spaceSize,
  Integer maxCapacity,
  String spaceDescription,
  Integer likeCount,
  Integer viewCount,
  Integer reviewCount) {
}
