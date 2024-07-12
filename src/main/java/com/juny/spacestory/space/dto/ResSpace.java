package com.juny.spacestory.space.dto;

import com.juny.spacestory.space.domain.category.ResSubCategory;
import com.juny.spacestory.space.domain.hashtag.ResHashtag;
import com.juny.spacestory.space.domain.option.ResOption;
import com.juny.spacestory.space.domain.realestate.ResRealEstate;
import java.time.LocalTime;
import java.util.List;

public record ResSpace(
  Long id,
  String name,
  String description,
  String reservationNotes,
  LocalTime openingTime,
  LocalTime closingTime,
  Integer hourlyRate,
  Integer spaceSize,
  Integer maxCapacity,
  Integer likeCount,
  Integer viewCount,
  Integer reviewCount,
  ResRealEstate realEstate,
  List<ResSubCategory> subCategories,
  List<ResOption> spaceOptions,
  List<ResHashtag> hashtags) {
}
