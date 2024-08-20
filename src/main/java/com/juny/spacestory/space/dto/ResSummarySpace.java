package com.juny.spacestory.space.dto;

import com.juny.spacestory.space.domain.hashtag.ResHashtag;
import java.util.List;

public record ResSummarySpace(
  Long id,
  String name,
  String representImage,
  Integer hourlyRate,
  Integer maxCapacity,
  Integer reviewCount,
  Integer likeCount,
  String dong,
  List<ResHashtag> hashtags
  ) {
}
